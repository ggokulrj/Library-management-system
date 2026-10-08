package com.library.service;

import com.library.entity.Book;
import com.library.exception.BusinessException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final TransactionRepository transactionRepository;

    public List<Book> getAll(String q) {
        return (q == null || q.isBlank()) ? bookRepository.findAll() : bookRepository.search(q.trim());
    }

    public Book getById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
    }

    public Book add(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn()))
            throw new BusinessException("A book with ISBN " + book.getIsbn() + " already exists");
        book.setId(null);
        book.setAvailableCopies(book.getTotalCopies()); // a new book starts with every copy on the shelf
        return bookRepository.save(book);
    }

    public Book update(Long id, Book in) {
        Book book = getById(id);
        int issued = book.getTotalCopies() - book.getAvailableCopies();
        if (in.getTotalCopies() < issued)
            throw new BusinessException("Total copies cannot be less than the " + issued + " copies currently issued");
        bookRepository.findByIsbn(in.getIsbn()).ifPresent(other -> {
            if (!other.getId().equals(id)) throw new BusinessException("Another book already uses ISBN " + in.getIsbn());
        });
        book.setTitle(in.getTitle());
        book.setAuthor(in.getAuthor());
        book.setIsbn(in.getIsbn());
        book.setCategory(in.getCategory());
        book.setTotalCopies(in.getTotalCopies());
        book.setAvailableCopies(in.getTotalCopies() - issued);
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        Book book = getById(id);
        if (transactionRepository.existsByBookId(id))
            throw new BusinessException("This book has issue history and cannot be deleted");
        bookRepository.delete(book);
    }
}
