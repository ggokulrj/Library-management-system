package com.library.controller;

import com.library.dto.IssueRequest;
import com.library.entity.BookTransaction;
import com.library.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public List<BookTransaction> getAll(@RequestParam(required = false) String status) {
        return transactionService.getAll(status);
    }

    @PostMapping("/issue")
    public BookTransaction issue(@Valid @RequestBody IssueRequest request) {
        return transactionService.issueBook(request.bookId(), request.memberId());
    }

    @PostMapping("/{id}/return")
    public BookTransaction returnBook(@PathVariable Long id) { return transactionService.returnBook(id); }
}
