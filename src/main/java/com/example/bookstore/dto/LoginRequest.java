package com.example.bookstore.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;


@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginRequest {


    @NotBlank(message = "Username is required.")
    private String username;


    @NotBlank(message = "Password is required")
    private String password;


}
