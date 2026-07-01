package com.startuphub.dto.request;

import com.startuphub.enums.CommentVisibility;
import jakarta.validation.constraints.NotBlank;

public record AddCommentRequest(

    @NotBlank(message = "Comment content is required")
    String content,

    CommentVisibility visibility,

    String attachmentUrl
) {}
