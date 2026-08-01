package com.example.bookstore.repository;

import com.example.bookstore.entity.Author;
import com.example.bookstore.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void findByIsbn_shouldReturnBook_whenBookExists() {
        String uniqueValue = uniqueValue();
        Book savedBook = saveBook(
                "Repository Test Book",
                "Repository Author " + uniqueValue,
                "ISBN-" + uniqueValue
        );

        Optional<Book> result =
                bookRepository.findByIsbn(savedBook.getIsbn());

        assertTrue(result.isPresent());
        assertEquals(savedBook.getId(), result.get().getId());
        assertEquals(savedBook.getTitle(), result.get().getTitle());
        assertEquals(savedBook.getAuthor().getName(),
                result.get().getAuthor().getName());
    }

    @Test
    void searchByKeyword_shouldFindBookByAuthorName() {
        String uniqueValue = uniqueValue();
        String authorName = "SearchAuthor" + uniqueValue;
        Book savedBook = saveBook(
                "Another Repository Book",
                authorName,
                "ISBN-" + uniqueValue
        );

        Page<Book> result = bookRepository
                .findByTitleContainingIgnoreCaseOrAuthor_NameContainingIgnoreCase(
                        authorName,
                        authorName,
                        PageRequest.of(0, 10)
                );

        assertEquals(1, result.getTotalElements());
        assertEquals(savedBook.getId(), result.getContent().get(0).getId());
        assertEquals(authorName,
                result.getContent().get(0).getAuthor().getName());
    }

    private Book saveBook(
            String title,
            String authorName,
            String isbn) {
        Author author = new Author();
        author.setName(authorName);
        Author savedAuthor = authorRepository.save(author);

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(savedAuthor);
        book.setIsbn(isbn);
        book.setPrice(new BigDecimal("29.99"));
        book.setStock(5);
        book.setCreatedAt(LocalDateTime.now());

        return bookRepository.saveAndFlush(book);
    }

    private String uniqueValue() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12);
    }
}
