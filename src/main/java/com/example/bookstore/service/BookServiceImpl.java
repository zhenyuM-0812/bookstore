package com.example.bookstore.service;


import com.example.bookstore.dto.BookRequestDto;
import com.example.bookstore.dto.BookResponseDto;
import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.ResourceNotFoundException;
import com.example.bookstore.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService{

    private final BookRepository bookRepository;


    private BookResponseDto toResponseDto(Book book){
        return BookResponseDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .stock(book.getStock())
                .createdAt(book.getCreatedAt())
                .build();
    }


    @Override
    @Transactional
    public BookResponseDto createBook(BookRequestDto request){
        bookRepository.findByIsbn(request.getIsbn())
                .ifPresent(existingBook ->{
                    throw new DuplicateResourceException(
                            "A book with ISBN" + request.getIsbn() + "already exists"
                    );
                });
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());
        book.setCreatedAt(LocalDateTime.now());

        Book savedBook = bookRepository.save(book);
        return toResponseDto(savedBook);
    }

    @Override
    @Transactional(readOnly= true)
    public BookResponseDto getBookById(Long id){
        Book book = findBookOrThrow(id);
        return toResponseDto(book);
    }

    @Override
    @Transactional(readOnly= true)
    public Page<BookResponseDto> getAllBooks(
            String keyword,
            Pageable pageable
    ){
        Page<Book> books;
        if(keyword == null || keyword.isBlank()){
            books = bookRepository.findAll(pageable);
        }else{
            books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword,keyword,pageable);

        }
        return books.map(this::toResponseDto);
    }

    @Override
    @Transactional
    public BookResponseDto updateBook(Long id, BookRequestDto request){
        Book book = findBookOrThrow(id);

        if(!book.getIsbn().equals(reuqest.getIsbn())){
            bookRepository.findByIsbn(request.getIsbn())
                    .ifPresent(existingBook ->{
                        throw new DuplicateResourceException(
                                "A book with ISBN" + request.getIsbn() + "already exists"
                        );

                    });

        }
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());

        Book updatedBook = bookRepository.save(book);
        return toResponseDto(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(Long id){
        Book book = findBookOrThrow(id);
        bookRepository.delete(book);
    }



    private Book findBookOrThrow(Long id){
        return bookRepository.findbyId(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book with id" + id + "not exists"
                        )
                );
    }
}
