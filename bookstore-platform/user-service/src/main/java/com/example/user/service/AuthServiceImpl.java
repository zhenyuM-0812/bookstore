package com.example.user.service;


import com.example.user.dto.LoginRequest;
import com.example.user.dto.LoginResponse;
import com.example.user.dto.RegisterRequest;
import com.example.user.dto.UserResponse;
import com.example.user.entity.Role;
import com.example.user.entity.User;
import com.example.user.exception.DuplicateResourceException;
import com.example.user.exception.ResourceNotFoundException;
import com.example.user.repository.UserRepository;
import com.example.user.security.JwtUtil;
import com.example.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

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



        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User " + userDetails.getUsername() + " was not found."
                        )
                );


        String token = jwtUtil.generateToken(userDetails, user.getId());

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
