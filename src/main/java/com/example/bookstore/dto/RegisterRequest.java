package com.example.bookstore.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class RegisterRequest {



    @NotBlank(message = "Username is required.")
    @Size(max = 100,
            message = "Username must less than 100 characters")
    private String username;


    @NotBlank(message = "E-mail is required.")
    @Email(message = "Email format is invalid")
    @Size(max = 255,
            message = "E-mail must less than 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72,
            message = "Password must contain between 8 and 72 characters")
    private String password;


}
