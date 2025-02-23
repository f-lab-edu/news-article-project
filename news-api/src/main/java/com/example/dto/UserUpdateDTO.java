package com.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class UserUpdateDTO {

    @NotNull
    private String username;
    @NotNull
    private String password;
    @Range(min = 1, max = 365)
    private Integer mailCycle;
}
