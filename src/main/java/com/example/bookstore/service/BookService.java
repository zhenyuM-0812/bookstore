package com.example.bookstore.service;


import com.example.bookstore.dto.BookRequestDto;
import com.example.bookstore.dto.BookResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookResponseDto createBook(BookRequestDto request);

    BookResponseDto getBookById(Long id);

    Page<BookResponseDto> getAllBooks(String keyword, Pageable pageable);

    BookResponseDto updateBook(Long id, BookRequestDto request);

    void deleteBook(Long id);
}
