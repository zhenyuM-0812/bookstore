package com.example.bookstore.controller;

import com.example.bookstore.dto.BookRequestDto;
import com.example.bookstore.dto.BookResponseDto;
import com.example.bookstore.security.JwtAuthenticationFilter;
import com.example.bookstore.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private BookResponseDto response;

    @BeforeEach
    void setUp() {
        response = BookResponseDto.builder()
                .id(1L)
                .title("Spring Boot in Action")
                .author("Craig Walls")
                .isbn("9781617292545")
                .price(new BigDecimal("39.99"))
                .stock(10)
                .createdAt(LocalDateTime.of(2026, 7, 31, 12, 0))
                .build();
    }

    @Test
    void createBook_shouldReturnCreatedBook() throws Exception {
        when(bookService.createBook(any(BookRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value(response.getTitle()))
                .andExpect(jsonPath("$.author").value(response.getAuthor()))
                .andExpect(jsonPath("$.isbn").value(response.getIsbn()));

        verify(bookService).createBook(any(BookRequestDto.class));
    }

    @Test
    void createBook_shouldReturnBadRequest_whenRequestIsInvalid()
            throws Exception {
        String invalidRequest = """
                {
                  "title": "",
                  "author": "Craig Walls",
                  "isbn": "9781617292545",
                  "price": 39.99,
                  "stock": 10
                }
                """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed for one or more fields."));

        verifyNoInteractions(bookService);
    }

    @Test
    void getBookById_shouldReturnBook() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value(response.getTitle()));

        verify(bookService).getBookById(1L);
    }

    @Test
    void getAllBooks_shouldReturnPageOfBooks() throws Exception {
        PageRequest pageable = PageRequest.of(0, 10);
        when(bookService.getAllBooks(eq("Spring"), any()))
                .thenReturn(new PageImpl<>(List.of(response), pageable, 1));

        mockMvc.perform(get("/api/books")
                        .param("keyword", "Spring")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title")
                        .value(response.getTitle()));

        verify(bookService).getAllBooks(eq("Spring"), any());
    }

    @Test
    void updateBook_shouldReturnUpdatedBook() throws Exception {
        when(bookService.updateBook(eq(1L), any(BookRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value(response.getTitle()));

        verify(bookService)
                .updateBook(eq(1L), any(BookRequestDto.class));
    }

    @Test
    void deleteBook_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBook(1L);
    }

    private String validRequestJson() {
        return """
                {
                  "title": "Spring Boot in Action",
                  "author": "Craig Walls",
                  "isbn": "9781617292545",
                  "price": 39.99,
                  "stock": 10
                }
                """;
    }
}
