package com.example.repository;

import com.example.domain.User;
import com.example.dto.UserDTO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryUserRepository implements UserRepository {

    private static Map<Long, User> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public User save(User user) {
        user.setId(++sequence);
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void updateUserInfo(Long id, UserDTO dto) {
        User user = findById(id).get();
        if (user != null) {
            user.setEmail(dto.getEmail());
            user.setUsername(dto.getUsername());
            user.setPassword(dto.getPassword());
        }
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public void clear() {
        store.clear();
    }

    public void resetSequence() {
        sequence = 0L;
    }

}
