package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);

    // Search across title, author, ISBN and category (case-insensitive)
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(b.category) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Book> search(@Param("q") String q);
}
