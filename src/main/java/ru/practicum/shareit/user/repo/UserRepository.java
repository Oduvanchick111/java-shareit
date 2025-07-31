package ru.practicum.shareit.user.repo;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;


public interface UserRepository {
    Collection<User> findAll();

    User save(User user);

    Optional<User> findUserById(Long id);

    User update(Long userId, User user);

    void delete(Long userId);

    void deleteAll();

    boolean existsByEmail(String email);
}
