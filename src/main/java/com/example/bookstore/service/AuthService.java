package com.example.bookstore.service;


import com.example.bookstore.dto.LoginRequest;
import com.example.bookstore.dto.LoginResponse;
import com.example.bookstore.dto.RegisterRequest;
import com.example.bookstore.dto.UserResponse;


public interface AuthService {

    void register(RegisterRequest request);


    LoginResponse login(LoginRequest request);

    UserResponse getCurrentUser(String username);


}
