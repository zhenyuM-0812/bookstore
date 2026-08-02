package com.example.user.dto;

import com.example.user.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponse {

    private Long id;

    private String username;

    private String email;

    private Role role;

    private LocalDateTime createdAt;
}