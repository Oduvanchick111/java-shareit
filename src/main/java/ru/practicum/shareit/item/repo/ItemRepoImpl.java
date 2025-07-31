package ru.practicum.shareit.item.repo;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repoJPA.UserRepoJpa;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Data
public class ItemRepoImpl implements ItemRepoInterface {

    private final Map<Long, Item> allItems = new HashMap<>();
    private final UserRepoJpa userRepoJpa;
    private final ItemRepoJpa itemRepoJpa;


    @Override
    public Item saveItem(Long ownerId, Item item) {
        User user = userRepoJpa.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        item.setId(getNextId());
        item.setOwner(user);
        allItems.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        Item itemDao = allItems.get(itemId);
        if (item.getName() != null) {
            itemDao.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemDao.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemDao.setAvailable(item.getAvailable());
        }
        return allItems.get(item.getId());
    }

    @Override
    public Optional<Item> getItemById(Long ownerId, Long itemId) {
        return Optional.ofNullable(allItems.get(itemId));
    }

    @Override
    public Collection<Item> getItemByUserId(Long ownerId) {
        return allItems.values().stream().filter(itemDao -> ownerId.equals(itemDao.getOwner().getId())).collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getItemOnText(Long ownerId, String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String searchText = text.toLowerCase();
        return allItems.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(searchText) ||
                        (item.getDescription().toLowerCase().contains(searchText))) && item.getAvailable().equals(true))
                .toList();
    }

    @Override
    public void deleteAllItems() {
        allItems.clear();
    }

    private Long getNextId() {
        Collection<Item> items = allItems.values();
        return items.isEmpty() ? 1L : items.stream()
                .mapToLong(Item::getId)
                .max()
                .orElse(0) + 1;
    }
}
