package com.example.bookstore.repository;

import com.example.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {

    Optional<Book> findByIsbn(String isbn);
    Page<Book> findByTitleContainingIgnoreCaseOrAuthor_NameContainingIgnoreCase(
            String titleKeyword,
            String authorKeyword,
            Pageable pageable
    );

}
