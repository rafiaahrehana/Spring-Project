package com.startuphub.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record ClientNoteRequest(

    @NotBlank(message = "Note content is required")
    String content,

    LocalDateTime followUpAt
) {}
