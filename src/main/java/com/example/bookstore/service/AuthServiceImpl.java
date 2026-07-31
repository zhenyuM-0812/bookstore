package com.example.bookstore.service;


import com.example.bookstore.dto.RegisterRequest;
import com.example.bookstore.entity.Role;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.DuplicateResourceException;
import com.example.bookstore.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    @Override
    @Transactional
    public void register(RegisterRequest request){

        if(userRepository.existsByUsername(request.getUsername())){
            throw new DuplicateResourceException(
                    "Username " + request.getUsername() + " already exists."
            );
        }

        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException(
                    "Email address " + request.getEmail() + " already exists."
            );
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(Role.USER);

        userRepository.save(user);

    }



}
