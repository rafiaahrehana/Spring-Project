package com.startuphub.dto.response;

import com.startuphub.enums.WalletTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletTransactionResponse(
    Long id,
    WalletTransactionType type,
    BigDecimal amount,
    BigDecimal balanceAfter,
    String reference,
    String notes,
    LocalDateTime transactedAt
) {}
