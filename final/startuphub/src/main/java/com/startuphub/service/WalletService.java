package com.startuphub.service;

import com.startuphub.dto.request.WalletTopUpRequest;
import com.startuphub.dto.response.WalletResponse;
import com.startuphub.dto.response.WalletTransactionResponse;
import com.startuphub.entity.Wallet;
import com.startuphub.enums.WalletTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface WalletService {

    WalletResponse getOrCreateWallet();

    WalletResponse topUp(WalletTopUpRequest request);

    Page<WalletTransactionResponse> getTransactions(WalletTransactionType type, Pageable pageable);

    /**
     * Internal method used by Invoice/Payment services.
     * Debits the wallet and records a ledger entry.
     */
    Wallet debit(Long companyId, BigDecimal amount, String reference, String notes);

    /**
     * Internal method used by Refund service.
     * Credits the wallet balance.
     */
    Wallet credit(Long companyId, BigDecimal amount, WalletTransactionType type,
                  String reference, String notes);
}
