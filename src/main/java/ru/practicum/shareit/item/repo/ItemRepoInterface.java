package ru.practicum.shareit.item.repo;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepoInterface {
    Item saveItem(Long ownerId, Item item);

    Item updateItem(Long itemId, Item item);

    Optional<Item> getItemById(Long ownerId, Long itemId);

    Collection<Item> getItemByUserId(Long ownerId);

    Collection<Item> getItemOnText(Long ownerId, String text);

    void deleteAllItems();
}
