package com.example.bookstore.dto;


import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;

}
