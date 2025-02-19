package com.example.repository;


import com.example.domain.User;
import com.example.dto.UserDTO;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    User save(User user);

    void updateUserInfo(Long id, UserDTO dto);

    void deleteById(Long id);

    void clear();
}
