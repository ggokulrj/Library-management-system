package com.library.service;

import com.library.entity.*;
import com.library.exception.BusinessException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private static final int LOAN_DAYS = 14;
    private static final int MAX_BOOKS_PER_MEMBER = 3;
    private static final int FINE_PER_DAY = 5;

    private final TransactionRepository transactionRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BookService bookService;
    private final MemberService memberService;

    // @Transactional: if any step fails, ALL database changes in this method are undone
    @Transactional
    public BookTransaction issueBook(Long bookId, Long memberId) {
        Book book = bookService.getById(bookId);
        Member member = memberService.getById(memberId);

        if (book.getAvailableCopies() <= 0)
            throw new BusinessException("No copies of '" + book.getTitle() + "' are available right now");
        if (transactionRepository.existsByBookIdAndMemberIdAndStatus(bookId, memberId, TransactionStatus.ISSUED))
            throw new BusinessException(member.getName() + " already has this book");
        if (transactionRepository.countByMemberIdAndStatus(memberId, TransactionStatus.ISSUED) >= MAX_BOOKS_PER_MEMBER)
            throw new BusinessException(member.getName() + " has reached the limit of " + MAX_BOOKS_PER_MEMBER + " books");

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        BookTransaction t = new BookTransaction();
        t.setBook(book);
        t.setMember(member);
        t.setIssueDate(LocalDate.now());
        t.setDueDate(LocalDate.now().plusDays(LOAN_DAYS));
        t.setStatus(TransactionStatus.ISSUED);
        return transactionRepository.save(t);
    }

    @Transactional
    public BookTransaction returnBook(Long transactionId) {
        BookTransaction t = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + transactionId));
        if (t.getStatus() == TransactionStatus.RETURNED)
            throw new BusinessException("This book has already been returned");

        LocalDate today = LocalDate.now();
        long lateDays = Math.max(0, ChronoUnit.DAYS.between(t.getDueDate(), today));
        t.setReturnDate(today);
        t.setFine((int) (lateDays * FINE_PER_DAY));
        t.setStatus(TransactionStatus.RETURNED);

        Book book = t.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        return transactionRepository.save(t);
    }

    // ----- Reports -----
    public List<BookTransaction> getAll(String status) {
        if (status == null || status.isBlank()) return transactionRepository.findAllByOrderByIdDesc();
        return transactionRepository.findByStatusOrderByIdDesc(TransactionStatus.valueOf(status.toUpperCase()));
    }

    public List<BookTransaction> getOverdue() {
        return transactionRepository.findByStatusAndDueDateBefore(TransactionStatus.ISSUED, LocalDate.now());
    }

    public Map<String, Object> getSummary() {
        List<Book> books = bookRepository.findAll();
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("bookTitles", books.size());
        s.put("totalCopies", books.stream().mapToInt(Book::getTotalCopies).sum());
        s.put("availableCopies", books.stream().mapToInt(Book::getAvailableCopies).sum());
        s.put("members", memberRepository.count());
        s.put("currentlyIssued", transactionRepository.countByStatus(TransactionStatus.ISSUED));
        s.put("overdue", getOverdue().size());
        return s;
    }
}
