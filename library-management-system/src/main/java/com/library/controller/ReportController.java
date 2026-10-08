package com.library.controller;

import com.library.entity.BookTransaction;
import com.library.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final TransactionService transactionService;

    @GetMapping("/summary")
    public Map<String, Object> summary() { return transactionService.getSummary(); }

    @GetMapping("/overdue")
    public List<BookTransaction> overdue() { return transactionService.getOverdue(); }
}
