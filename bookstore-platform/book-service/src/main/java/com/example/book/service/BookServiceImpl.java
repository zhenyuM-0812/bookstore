package com.example.book.service;

import com.example.book.dto.BookRequestDto;
import com.example.book.dto.BookResponseDto;
import com.example.book.dto.BookStockResponseDto;
import com.example.book.entity.Author;
import com.example.book.entity.Book;
import com.example.book.exception.DuplicateResourceException;
import com.example.book.exception.InsufficientStockException;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.AuthorRepository;
import com.example.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    private BookResponseDto toResponseDto(Book book) {
        return BookResponseDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor().getName())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .stock(book.getStock())
                .createdAt(book.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public BookResponseDto createBook(BookRequestDto request) {
        bookRepository.findByIsbn(request.getIsbn())
                .ifPresent(existingBook -> {
                    throw new DuplicateResourceException(
                            "A book with ISBN " + request.getIsbn() + " already exists"
                    );
                });

        Author author = authorRepository.findByName(request.getAuthor())
                .orElseGet(() -> {
                    Author newAuthor = new Author();
                    newAuthor.setName(request.getAuthor());
                    return authorRepository.save(newAuthor);
                });

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(author);
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());
        book.setCreatedAt(LocalDateTime.now());

        Book savedBook = bookRepository.save(book);
        return toResponseDto(savedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDto getBookById(Long id) {
        Book book = findBookOrThrow(id);
        return toResponseDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponseDto> getAllBooks(
            String keyword,
            Pageable pageable) {

        Page<Book> books;

        if (keyword == null || keyword.isBlank()) {
            books = bookRepository.findAll(pageable);
        } else {
            books = bookRepository
                    .findByTitleContainingIgnoreCaseOrAuthor_NameContainingIgnoreCase(
                            keyword,
                            keyword,
                            pageable
                    );
        }

        return books.map(this::toResponseDto);
    }

    @Override
    @Transactional
    public BookResponseDto updateBook(Long id, BookRequestDto request) {
        Book book = findBookOrThrow(id);

        if (!book.getIsbn().equals(request.getIsbn())) {
            bookRepository.findByIsbn(request.getIsbn())
                    .ifPresent(existingBook -> {
                        throw new DuplicateResourceException(
                                "A book with ISBN " + request.getIsbn() + " already exists"
                        );
                    });
        }

        Author author = authorRepository.findByName(request.getAuthor())
                .orElseGet(() -> {
                    Author newAuthor = new Author();
                    newAuthor.setName(request.getAuthor());
                    return authorRepository.save(newAuthor);
                });

        book.setTitle(request.getTitle());
        book.setAuthor(author);
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());

        Book updatedBook = bookRepository.save(book);
        return toResponseDto(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = findBookOrThrow(id);
        bookRepository.delete(book);
    }

    @Override
    @Transactional(readOnly = true)
    public BookStockResponseDto getBookStock(Long id){
        Book book = findBookOrThrow(id);

        return BookStockResponseDto.builder()
                .bookId(book.getId())
                .price(book.getPrice())
                .stock(book.getStock())
                .build();
    }

    @Override
    @Transactional
    public BookStockResponseDto reserveStock(Long id, Integer quantity){

        Book book = findBookOrThrow(id);

        if(book.getStock()<quantity){
            throw new InsufficientStockException(
                    "Insufficient stock for book with id "
                    + id
                    +". Available stock: "
                    + book.getStock()
                    + ", requested quantity: "
                    + quantity
            );
        }

        book.setStock(book.getStock() - quantity);

        Book updatedBook = bookRepository.save(book);

        return BookStockResponseDto.builder()
                .bookId(updatedBook.getId())
                .price(updatedBook.getPrice())
                .stock(updatedBook.getStock())
                .build();

    }










    private Book findBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book with id " + id + " does not exist"
                        )
                );
    }
}
