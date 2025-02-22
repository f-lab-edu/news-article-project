package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class EnrollUserDTO {
    @Email
    @NotNull
    String email;
    @NotNull
    String username;
    @NotNull
    String password;
}
