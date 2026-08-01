package com.example.bookstore.service;

import com.example.bookstore.dto.BookRequestDto;
import com.example.bookstore.dto.BookResponseDto;
import com.example.bookstore.entity.Author;
import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.DuplicateResourceException;
import com.example.bookstore.exception.ResourceNotFoundException;
import com.example.bookstore.repository.AuthorRepository;
import com.example.bookstore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private BookRequestDto request;
    private Author author;
    private Book book;

    @BeforeEach
    void setUp() {
        request = BookRequestDto.builder()
                .title("Spring Boot in Action")
                .author("Craig Walls")
                .isbn("9781617292545")
                .price(new BigDecimal("39.99"))
                .stock(10)
                .build();

        author = new Author();
        author.setId(1L);
        author.setName(request.getAuthor());

        book = new Book();
        book.setId(1L);
        book.setTitle(request.getTitle());
        book.setAuthor(author);
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());
        book.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createBook_shouldReturnCreatedBook_whenIsbnIsUniqueAndAuthorExists() {
        when(bookRepository.findByIsbn(request.getIsbn()))
                .thenReturn(Optional.empty());
        when(authorRepository.findByName(request.getAuthor()))
                .thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> {
                    Book bookToSave = invocation.getArgument(0);
                    bookToSave.setId(1L);
                    return bookToSave;
                });

        BookResponseDto response = bookService.createBook(request);

        assertBookResponse(response);
        verify(authorRepository, never()).save(any(Author.class));
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void createBook_shouldCreateAuthor_whenAuthorDoesNotExist() {
        when(bookRepository.findByIsbn(request.getIsbn()))
                .thenReturn(Optional.empty());
        when(authorRepository.findByName(request.getAuthor()))
                .thenReturn(Optional.empty());
        when(authorRepository.save(any(Author.class)))
                .thenAnswer(invocation -> {
                    Author authorToSave = invocation.getArgument(0);
                    authorToSave.setId(1L);
                    return authorToSave;
                });
        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> {
                    Book bookToSave = invocation.getArgument(0);
                    bookToSave.setId(1L);
                    return bookToSave;
                });

        BookResponseDto response = bookService.createBook(request);

        assertBookResponse(response);
        verify(authorRepository).save(any(Author.class));
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void createBook_shouldThrowException_whenIsbnAlreadyExists() {
        when(bookRepository.findByIsbn(request.getIsbn()))
                .thenReturn(Optional.of(book));

        assertThrows(
                DuplicateResourceException.class,
                () -> bookService.createBook(request)
        );

        verifyNoInteractions(authorRepository);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void getBookById_shouldReturnBook_whenBookExists() {
        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        BookResponseDto response = bookService.getBookById(1L);

        assertBookResponse(response);
        verify(bookRepository).findById(1L);
    }

    @Test
    void getBookById_shouldThrowException_whenBookDoesNotExist() {
        when(bookRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.getBookById(99L)
        );
    }

    @Test
    void getAllBooks_shouldUseFindAll_whenKeywordIsBlank() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> books = new PageImpl<>(List.of(book), pageable, 1);
        when(bookRepository.findAll(pageable)).thenReturn(books);

        Page<BookResponseDto> response =
                bookService.getAllBooks(null, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(book.getTitle(), response.getContent().get(0).getTitle());
        verify(bookRepository).findAll(pageable);
    }

    @Test
    void getAllBooks_shouldSearchTitleAndAuthor_whenKeywordIsPresent() {
        String keyword = "Spring";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> books = new PageImpl<>(List.of(book), pageable, 1);
        when(bookRepository
                .findByTitleContainingIgnoreCaseOrAuthor_NameContainingIgnoreCase(
                        keyword,
                        keyword,
                        pageable
                ))
                .thenReturn(books);

        Page<BookResponseDto> response =
                bookService.getAllBooks(keyword, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(book.getTitle(), response.getContent().get(0).getTitle());
        verify(bookRepository)
                .findByTitleContainingIgnoreCaseOrAuthor_NameContainingIgnoreCase(
                        keyword,
                        keyword,
                        pageable
                );
    }

    @Test
    void updateBook_shouldUpdateAndReturnBook_whenBookExists() {
        BookRequestDto updateRequest = BookRequestDto.builder()
                .title("Updated Spring Boot")
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .price(new BigDecimal("49.99"))
                .stock(20)
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(authorRepository.findByName(updateRequest.getAuthor()))
                .thenReturn(Optional.of(author));
        when(bookRepository.save(book)).thenReturn(book);

        BookResponseDto response =
                bookService.updateBook(1L, updateRequest);

        assertEquals(updateRequest.getTitle(), response.getTitle());
        assertEquals(updateRequest.getPrice(), response.getPrice());
        assertEquals(updateRequest.getStock(), response.getStock());
        verify(bookRepository).save(book);
    }

    @Test
    void deleteBook_shouldDeleteBook_whenBookExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookRepository).delete(book);
    }

    private void assertBookResponse(BookResponseDto response) {
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getAuthor(), response.getAuthor());
        assertEquals(request.getIsbn(), response.getIsbn());
        assertEquals(request.getPrice(), response.getPrice());
        assertEquals(request.getStock(), response.getStock());
        assertNotNull(response.getCreatedAt());
    }
}
