package com.startuphub.dto.response;

import com.startuphub.enums.CommentVisibility;

import java.time.LocalDateTime;

public record RequestCommentResponse(
    Long id,
    String content,
    CommentVisibility visibility,
    String attachmentUrl,
    Long authorId,
    String authorName,
    LocalDateTime createdAt
) {}
