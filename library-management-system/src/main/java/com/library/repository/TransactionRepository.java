package com.library.repository;

import com.library.entity.BookTransaction;
import com.library.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<BookTransaction, Long> {

    List<BookTransaction> findAllByOrderByIdDesc();

    List<BookTransaction> findByStatusOrderByIdDesc(TransactionStatus status);

    List<BookTransaction> findByStatusAndDueDateBefore(TransactionStatus status, LocalDate date);

    long countByStatus(TransactionStatus status);

    long countByMemberIdAndStatus(Long memberId, TransactionStatus status);

    boolean existsByBookIdAndMemberIdAndStatus(Long bookId, Long memberId, TransactionStatus status);

    boolean existsByBookId(Long bookId);

    boolean existsByMemberId(Long memberId);
}
