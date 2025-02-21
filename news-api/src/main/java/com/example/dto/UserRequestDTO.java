package com.example.dto;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String username;
    private String password;
    private Integer mail_cycle;
}
