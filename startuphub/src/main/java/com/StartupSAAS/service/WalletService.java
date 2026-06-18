package com.StartupSAAS.service;

import com.StartupSAAS.dto.response.WalletResponseDTO;

public interface WalletService {

    WalletResponseDTO getByClient(Long clientId);
    WalletResponseDTO credit(Long clientId, Double amount, String reference);
    WalletResponseDTO debit(Long clientId, Double amount, String reference);
}
