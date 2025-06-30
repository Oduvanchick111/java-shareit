package ru.practicum.shareit.item.repo;

import ru.practicum.shareit.item.model.ItemDao;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepoInterface {
    ItemDao saveItem(Long ownerId, ItemDao itemDao);

    ItemDao updateItem(Long itemId, ItemDao item);

    Optional<ItemDao> getItemById(Long ownerId, Long itemId);

    Collection<ItemDao> getItemByUserId(Long ownerId);

    Collection<ItemDao> getItemOnText(Long ownerId, String text);

    void deleteAllItems();
}
