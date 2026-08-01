package com.example.bookstore.security;

import com.example.bookstore.controller.BookController;
import com.example.bookstore.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import({
        SecurityConfig.class,
        JwtUtil.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class,
        JwtAccessDeniedHandler.class
})
@TestPropertySource(properties = {
        "jwt.secret=Ym9va3N0b3JlLWRldi1qd3Qtc2VjcmV0LWtleS0zMi1ieXRlcyE=",
        "jwt.expiration-ms=3600000"
})
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    void createBook_shouldReturnUnauthorized_whenJwtIsMissing()
            throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookRequestJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value(
                        "Authentication is required to access this resource."
                ));

        verifyNoInteractions(bookService);
    }

    @Test
    void createBook_shouldReturnForbidden_whenUserRoleUsesAdminEndpoint()
            throws Exception {
        UserDetails userDetails = User.withUsername("test-user")
                .password("encoded-password")
                .roles("USER")
                .build();

        when(userDetailsService.loadUserByUsername("test-user"))
                .thenReturn(userDetails);

        String token = jwtUtil.generateToken(userDetails);

        mockMvc.perform(post("/api/books")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value(
                        "You do not have permission to access this resource."
                ));

        verifyNoInteractions(bookService);
    }

    private String validBookRequestJson() {
        return """
                {
                  "title": "Spring Security in Action",
                  "author": "Laurentiu Spilca",
                  "isbn": "9781617297731",
                  "price": 44.99,
                  "stock": 10
                }
                """;
    }
}
