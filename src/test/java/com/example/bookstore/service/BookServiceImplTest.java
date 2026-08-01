package com.example.bookstore.service;

import com.example.bookstore.dto.BookRequestDto;
import com.example.bookstore.dto.BookResponseDto;
import com.example.bookstore.entity.Author;
import com.example.bookstore.entity.Book;
import com.example.bookstore.repository.AuthorRepository;
import com.example.bookstore.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void createBook_shouldReturnCreatedBook_whenIsbnIsUniqueAndAuthorExists(){
        BookRequestDto request = BookRequestDto.builder()
                .title("Test case 1")
                .author("Author one")
                .isbn("1234567890123")
                .price(new BigDecimal("29.99"))
                .stock(10)
                .build();

        Author existingAuthor = new Author();
        existingAuthor.setId(1L);
        existingAuthor.setName("Test Example One");


        when(bookRepository.findByIsbn(request.getIsbn()))
                .thenReturn(Optional.empty());

        when(authorRepository.findByName(request.getAuthor()))
                .thenReturn(Optional.of(existingAuthor));

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> {
                    Book book = invocation.getArgument(0);
                    book.setId(1L);
                    return book;
                });


        BookResponseDto response =
                bookService.createBook(request);


        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getAuthor(), response.getAuthor());
        assertEquals(request.getIsbn(), response.getIsbn());
        assertEquals(request.getPrice(), response.getPrice());
        assertEquals(request.getStock(), response.getStock());
        assertNotNull(response.getCreatedAt());

        verify(bookRepository).findByIsbn(request.getIsbn());
        verify(authorRepository).findByName(request.getAuthor());
        verify(authorRepository, never()).save(any(Author.class));
        verify(bookRepository).save(any(Book.class));
    }

}
