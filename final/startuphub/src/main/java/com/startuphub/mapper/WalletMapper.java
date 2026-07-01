package com.startuphub.mapper;

import com.startuphub.dto.response.WalletResponse;
import com.startuphub.dto.response.WalletTransactionResponse;
import com.startuphub.entity.Wallet;
import com.startuphub.entity.WalletTransaction;

public final class WalletMapper {

    private WalletMapper() {}

    public static WalletResponse toResponse(Wallet w) {
        return new WalletResponse(
            w.getId(),
            w.getBalance(),
            w.getCreditBalance(),
            w.getTotalAvailable(),
            w.getCurrency()
        );
    }

    public static WalletTransactionResponse toTransactionResponse(WalletTransaction t) {
        return new WalletTransactionResponse(
            t.getId(),
            t.getType(),
            t.getAmount(),
            t.getBalanceAfter(),
            t.getReference(),
            t.getNotes(),
            t.getTransactedAt()
        );
    }
}
