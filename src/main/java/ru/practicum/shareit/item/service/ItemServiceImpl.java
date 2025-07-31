package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponseForItem;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repo.BookingRepoJpa;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepoJpa;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repoJPA.UserRepoJpa;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepoJpa itemRepo;
    private final UserRepoJpa userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepoJpa bookingRepoJpa;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemResponseDto saveItem(Long userId, ItemRequestDto itemRequestDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        ItemRequest request = null;
        if (itemRequestDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemRequestDto.getRequestId())
                    .orElse(null);
        }
        Item item = ItemMapper.toItemDao(itemRequestDto, owner, request);
        Item savedItem = itemRepo.save(item);
        return ItemMapper.toItemResponseDto(savedItem);
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Long userId, Long itemId, ItemRequestForUpdateDto itemUpdateDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        Item item = itemRepo.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (!item.getOwner().equals(owner)) {
            throw new ValidateException("У вас нет прав на изменения данного айтема");
        }
        if (itemUpdateDto.getName() != null) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }
        return ItemMapper.toItemResponseDto(item);
    }

    @Override
    public ItemResponseDto getById(Long userId, Long itemId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        Item item = itemRepo.findById(itemId).orElseThrow(() -> new NotFoundException("Такой товар не найден"));

        List<CommentDto> comments = commentRepository.findByItemId(itemId)
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());

        BookingResponseForItem lastBookingDto = null;
        BookingResponseForItem nextBookingDto = null;

        if (item.getOwner() != null && owner.getId().equals(item.getOwner().getId())) {
            Booking last = bookingRepoJpa.findTopByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                    itemId, LocalDateTime.now(), Status.APPROVED);
            Booking next = bookingRepoJpa.findTopByItemIdAndStartAfterAndStatusOrderByStartAsc(
                    itemId, LocalDateTime.now(), Status.APPROVED);

            lastBookingDto = BookingMapper.toBookingResponseDtoForItem(last);
            nextBookingDto = BookingMapper.toBookingResponseDtoForItem(next);
        }

        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item);
        itemResponseDto.setComments(comments);
        itemResponseDto.setLastBooking(lastBookingDto);
        itemResponseDto.setNextBooking(nextBookingDto);

        return itemResponseDto;
    }

    @Override
    public Collection<ItemResponseDto> getItemsByUserId(Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        Collection<Item> items = itemRepo.findByOwnerIdOrderByIdAsc(ownerId);
        return items.stream().map(ItemMapper::toItemResponseDto).collect(Collectors.toList());
    }

    @Override
    public Collection<ItemResponseDto> getItemOnText(Long userId, String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        Collection<Item> items = itemRepo.getItemOnText(text);
        return items.stream().map(ItemMapper::toItemResponseDto).collect(Collectors.toList());
    }

    @Override
    public void deleteAllItems() {
        itemRepo.deleteAll();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найдн"));
        Item item = itemRepo.findById(itemId).orElseThrow(() -> new NotFoundException("Товар не найден"));
        boolean hasBooking = bookingRepoJpa.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, Status.APPROVED, LocalDateTime.now());
        if (!hasBooking) {
            throw new ValidateException("Пользователь не бронировал вещь");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toDto(commentRepository.save(comment));
    }

}
