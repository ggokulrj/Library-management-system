package com.library.dto;

import jakarta.validation.constraints.NotNull;

public record IssueRequest(
        @NotNull(message = "Select a book") Long bookId,
        @NotNull(message = "Select a member") Long memberId) {
}
