package com.example.repository;


import com.example.domain.User;
import com.example.dto.UserRequestDTO;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    User save(User user);

    void updateUserInfo(Long id, UserRequestDTO dto);

    void deleteById(Long id);

    void clear();
}
