package com.example.repository.memory;

import com.example.domain.User;
import com.example.dto.UserRequestDTO;
import com.example.repository.UserRepository;
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
    public void updateUserInfo(Long id, UserRequestDTO dto) throws Exception{
        User user = findById(id).get();
        if (user != null) {
            user.setEmail(dto.getEmail());
            user.setUsername(dto.getUsername());
            user.setPassword(dto.getPassword());
        } else {
            throw new Exception();
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

    public boolean isValid(UserRequestDTO dto) {
        long count = store.values().stream().filter(a -> {
            return (a.getEmail().equals(dto.getEmail()) || a.getUsername().equals(dto.getUsername()));
        }).count();

        if (count == 0) {
            return true;
        }
        return false;
    }

}
