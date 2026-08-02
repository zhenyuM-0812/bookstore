package com.example.bookstore.service;


import com.example.bookstore.dto.LoginRequest;
import com.example.bookstore.dto.LoginResponse;
import com.example.bookstore.dto.RegisterRequest;
import com.example.bookstore.dto.UserResponse;
import com.example.bookstore.entity.Role;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.DuplicateResourceException;
import com.example.bookstore.exception.ResourceNotFoundException;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


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

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request){
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );
        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();





        String token = jwtUtil.generateToken(userDetails);

        return LoginResponse.builder()
                .token(token)
                .build();

    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User " + username + " was not found."
                        )
                );

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());

        return response;
    }


}
