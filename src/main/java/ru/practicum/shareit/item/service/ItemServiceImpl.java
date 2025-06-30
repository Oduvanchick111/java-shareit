package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.ItemRepoJpa.ItemRepoJpa;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestForUpdateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepoImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.user.repoJPA.UserRepoJpa;


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepoJpa itemRepo;
    private final UserRepoJpa userRepository;

    @Override
    public ItemResponseDto saveItem(Long userId, ItemRequestDto itemRequestDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        Item item = ItemMapper.toItemDao(itemRequestDto);
        itemRepo.save(item);
        return ItemMapper.toItemResponseDto(item);
    }

    @Override
    public ItemResponseDto updateItem(Long userId, Long itemId, ItemRequestForUpdateDto itemUpdateDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        Item item = itemRepo.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (!item.getOwnerId().equals(userId)) {
            throw new ValidateException("У вас нет прав на изменения данного айтема");
        }
        Item updatedItem = itemRepo.save(item);
        return ItemMapper.toItemResponseDto(updatedItem);
    }

    @Override
    public ItemResponseDto getItemById(Long userId, Long itemId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        Item item = itemRepo.findById(itemId).orElseThrow(() -> new NotFoundException("Такой товар не найден"));
        return ItemMapper.toItemResponseDto(item);
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

////    @Override
////    public ItemResponseDto updateItem(Long userId, Long itemId, ItemRequestForUpdateDto item) {
////        User owner = userRepository.findById(userId)
////                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
////        Item itemDao = itemRepo.findById(userId, itemId)
////                .orElseThrow(() -> new NotFoundException("Товар не найден"));
////        if (!itemDao.getOwnerId().equals(owner.getId())) {
////            throw new NotFoundException("Нельзя изменять чужой товар");
////        }
////        Item updatedItem = ItemMapper.toItemDaoUpdate(item);
////        itemRepo.updateItem(itemId, updatedItem);
////        return ItemMapper.toItemResponseDto(updatedItem);
////    }
////
////    @Override
////    public ItemResponseDto getItemById(Long userId, Long itemId) {
////        return itemRepo.getItemById(userId, itemId)
////                .map(ItemMapper::toItemResponseDto)
////                .orElseThrow(() -> new NotFoundException("Такой товар не найден"));
////    }
////
////    @Override
////    public Collection<ItemResponseDto> getItemsByUserId(Long ownerId) {
////        if (userRepository.findUserById(ownerId).isEmpty()) {
////            throw new ValidateException("Такого пользователя не существует");
////        } else {
////            return itemRepo.getItemByUserId(ownerId).stream()
////                    .map(ItemMapper::toItemResponseDto)
////                    .toList();
////        }
////    }
////
////    @Override
////    public Collection<ItemResponseDto> getItemOnText(Long userId, String text) {
////        User user = userRepository.findUserById(userId)
////                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
////        return itemRepo.getItemOnText(userId, text).stream().map(ItemMapper::toItemResponseDto).toList();
//    }
//
//    @Override
//    public void deleteAllItems() {
//        userRepository.deleteAll();
//    }
}
