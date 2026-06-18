package com.StartupSAAS.service;

import com.StartupSAAS.dto.response.WalletResponse;

public interface WalletService {
    WalletResponse getWalletByCompany(Long companyId);
    WalletResponse creditWallet(Long companyId, Double amount);
    WalletResponse debitWallet(Long companyId, Double amount);
}
