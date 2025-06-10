package ru.practicum.shareit.user.repo;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.UserDao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, UserDao> users = new HashMap<>();

    @Override
    public Collection<UserDao> findAll() {
        return users.values();
    }

    @Override
    public UserDao save(UserDao user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<UserDao> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public UserDao update(Long userId, UserDao user) {
        UserDao existingUser = users.get(userId);
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        return existingUser;
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    @Override
    public void deleteAll() {
        users.clear();
    }

    @Override
    public boolean existsByEmail(String email) {
        return findAll().stream().anyMatch(userDao -> email.equals(userDao.getEmail()));
    }

    private Long getNextId() {
        Collection<UserDao> users = findAll();
        return users.isEmpty() ? 1L : users.stream()
                .mapToLong(UserDao::getId)
                .max()
                .orElse(0) + 1;
    }
}
