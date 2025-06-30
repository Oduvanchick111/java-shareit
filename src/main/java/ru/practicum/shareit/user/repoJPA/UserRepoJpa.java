package ru.practicum.shareit.user.repoJPA;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;


public interface UserRepoJpa extends JpaRepository<User, Long> {
}