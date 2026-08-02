package com.example.book.service;

import com.example.book.dto.BookRequestDto;
import com.example.book.dto.BookResponseDto;
import com.example.book.dto.BookStockResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookResponseDto createBook(BookRequestDto request);

    BookResponseDto getBookById(Long id);

    Page<BookResponseDto> getAllBooks(String keyword, Pageable pageable);

    BookResponseDto updateBook(Long id, BookRequestDto request);

    void deleteBook(Long id);

    BookStockResponseDto getBookStock(Long id);


    BookStockResponseDto reserveStock(Long id, Integer quantity);





}
