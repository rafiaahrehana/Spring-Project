package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.WalletMapper;
import com.StartupSAAS.dto.response.WalletResponseDTO;
import com.StartupSAAS.entity.Client;
import com.StartupSAAS.entity.Wallet;
import com.StartupSAAS.entity.WalletTransaction;
import com.StartupSAAS.enums.WalletTransactionType;
import com.StartupSAAS.repository.ClientRepository;
import com.StartupSAAS.repository.WalletRepository;
import com.StartupSAAS.repository.WalletTransactionRepository;
import com.StartupSAAS.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final ClientRepository clientRepository;

    @Override
    @Transactional(readOnly = true)
    public WalletResponseDTO getByClient(Long clientId) {
        Wallet wallet = walletRepository.findByClientId(clientId)
                .orElseGet(() -> createWallet(clientId));
        return WalletMapper.toDTO(wallet);
    }

    @Transactional
    @Override
    public WalletResponseDTO credit(Long clientId, Double amount, String reference) {
        Wallet wallet = walletRepository.findByClientId(clientId)
                .orElseGet(() -> createWallet(clientId));

        wallet.setBalance(wallet.getBalance() + amount);
        Wallet saved = walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(saved);
        tx.setAmount(amount);
        tx.setType(WalletTransactionType.CREDIT);
        tx.setBalanceAfter(saved.getBalance());
        tx.setReference(reference);
        walletTransactionRepository.save(tx);

        return WalletMapper.toDTO(saved);
    }

    @Transactional
    @Override
    public WalletResponseDTO debit(Long clientId, Double amount, String reference) {
        Wallet wallet = walletRepository.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException(
                        "Wallet not found for client: " + clientId));

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient wallet balance");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        Wallet saved = walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(saved);
        tx.setAmount(amount);
        tx.setType(WalletTransactionType.DEBIT);
        tx.setBalanceAfter(saved.getBalance());
        tx.setReference(reference);
        walletTransactionRepository.save(tx);

        return WalletMapper.toDTO(saved);
    }

    private Wallet createWallet(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException(
                        "Client not found with id: " + clientId));
        Wallet wallet = new Wallet();
        wallet.setClient(client);
        wallet.setBalance(0.0);
        return walletRepository.save(wallet);
    }
}
