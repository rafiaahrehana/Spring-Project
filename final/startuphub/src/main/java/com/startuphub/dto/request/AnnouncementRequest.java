package com.startuphub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AnnouncementRequest(

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    String title,

    @NotBlank(message = "Body is required")
    String body,

    LocalDateTime expiresAt,

    boolean notifyAll
) {}
