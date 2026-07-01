package com.startuphub.dto.response;

import java.math.BigDecimal;

public record WalletResponse(
    Long id,
    BigDecimal balance,
    BigDecimal creditBalance,
    BigDecimal totalAvailable,
    String currency
) {}
