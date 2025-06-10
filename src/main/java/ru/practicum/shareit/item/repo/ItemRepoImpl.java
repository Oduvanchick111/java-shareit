package ru.practicum.shareit.item.repo;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.ItemDao;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Data
public class ItemRepoImpl implements ItemRepoInterface {

    private final Map<Long, ItemDao> allItems = new HashMap<>();

    @Override
    public ItemDao saveItem(Long ownerId, ItemDao itemDao) {
        itemDao.setId(getNextId());
        itemDao.setOwnerId(ownerId);
        allItems.put(itemDao.getId(), itemDao);
        return itemDao;
    }

    @Override
    public ItemDao updateItem(Long itemId, ItemDao item) {
        ItemDao itemDao = allItems.get(itemId);
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
    public Optional<ItemDao> getItemById(Long ownerId, Long itemId) {
        return Optional.ofNullable(allItems.get(itemId));
    }

    @Override
    public Collection<ItemDao> getItemByUserId(Long ownerId) {
        return allItems.values().stream().filter(itemDao -> ownerId.equals(itemDao.getOwnerId())).collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDao> getItemOnText(Long ownerId, String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String searchText = text.toLowerCase();
        return allItems.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(searchText) ||
                        (item.getDescription().toLowerCase().contains(searchText))) && item.getAvailable().equals(true))
                .toList();
    }

    private Long getNextId() {
        Collection<ItemDao> items = allItems.values();
        return items.isEmpty() ? 1L : items.stream()
                .mapToLong(ItemDao::getId)
                .max()
                .orElse(0) + 1;
    }
}
