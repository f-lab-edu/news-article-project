package com.example.repository;

import com.example.domain.User;
import com.example.dto.UserDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemoryUserRepositoryTest {

    MemoryUserRepository repository = new MemoryUserRepository();

    @AfterEach
    void clear() {
        repository.clear();
        repository.resetSequence();
    }

    @Test
    void save() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("lee");
        user.setPassword("securepassword");

        repository.save(user);

        User result = repository.findById(user.getId()).get();
        assertThat(result).isEqualTo(user);
    }

    @Test
    void findById() {
        User user1 = new User();
        user1.setEmail("test@example.com");
        user1.setUsername("lee");
        user1.setPassword("securepassword");

        repository.save(user1);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setUsername("kim");
        user2.setPassword("securepassword");

        repository.save(user2);

        User result = repository.findById(user1.getId()).get();

        assertThat(result).isEqualTo(user1);
    }

    @Test
    void updateUserInfo() {
        User user1 = new User();
        user1.setEmail("test@example.com");
        user1.setUsername("lee");
        user1.setPassword("securepassword");
        repository.save(user1);
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setUsername("newUsername");
        userDTO.setPassword("newPassword");

        repository.updateUserInfo(user1.getId(), userDTO);

        assertThat(user1.getUsername()).isEqualTo("newUsername");
        assertThat(user1.getPassword()).isEqualTo("newPassword");
    }

    @Test
    void deleteById() {
        User user1 = new User();
        user1.setEmail("test@example.com");
        user1.setUsername("lee");
        user1.setPassword("securepassword");
        repository.save(user1);

        repository.deleteById(user1.getId());

        Optional<User> find = repository.findById(user1.getId());
        assertThat(find).isEmpty();
    }

}