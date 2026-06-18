package com.StartupSAAS.serviceImpl;

import com.StartupSAAS.dto.mapper.WalletMapper;
import com.StartupSAAS.dto.response.WalletResponse;
import com.StartupSAAS.entity.Wallet;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.exception.ResourceNotFoundException;
import com.StartupSAAS.repository.WalletRepository;
import com.StartupSAAS.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public WalletResponse getWalletByCompany(Long companyId) {
        return WalletMapper.toDTO(
                walletRepository.findByCompanyId(companyId)
                        .orElseThrow(() -> new ResourceNotFoundException("Wallet not found")));
    }

    @Override
    @Transactional
    public WalletResponse creditWallet(Long companyId, Double amount) {
        Wallet wallet = walletRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (amount <= 0)
            throw new BadRequestException("Amount must be greater than 0");

        wallet.setBalance(wallet.getBalance() + amount);
        return WalletMapper.toDTO(wallet);
    }

    @Override
    @Transactional
    public WalletResponse debitWallet(Long companyId, Double amount) {
        Wallet wallet = walletRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (amount <= 0)
            throw new BadRequestException("Amount must be greater than 0");

        if (wallet.getBalance() < amount)
            throw new BadRequestException("Insufficient wallet balance");

        wallet.setBalance(wallet.getBalance() - amount);
        return WalletMapper.toDTO(wallet);
    }
}
