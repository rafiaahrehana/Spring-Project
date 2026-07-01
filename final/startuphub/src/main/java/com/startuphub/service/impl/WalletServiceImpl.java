package com.startuphub.service.impl;

import com.startuphub.dto.request.WalletTopUpRequest;
import com.startuphub.dto.response.WalletResponse;
import com.startuphub.dto.response.WalletTransactionResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.Wallet;
import com.startuphub.entity.WalletTransaction;
import com.startuphub.enums.WalletTransactionType;
import com.startuphub.exception.BadRequestException;
import com.startuphub.mapper.WalletMapper;
import com.startuphub.repository.WalletRepository;
import com.startuphub.repository.WalletTransactionRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository            walletRepository;
    private final WalletTransactionRepository txRepository;
    private final SecurityUtil                securityUtil;

    @Override
    @Transactional
    public WalletResponse getOrCreateWallet() {
        Long companyId = requireCompanyId();
        Wallet wallet = walletRepository.findByCompanyId(companyId)
            .orElseGet(() -> createWallet(companyId));
        return WalletMapper.toResponse(wallet);
    }

    @Override
    @Transactional
    public WalletResponse topUp(WalletTopUpRequest request) {
        Long companyId = requireCompanyId();
        Wallet wallet = walletRepository.findByCompanyId(companyId)
            .orElseGet(() -> createWallet(companyId));

        wallet.setBalance(wallet.getBalance().add(request.amount()));
        walletRepository.save(wallet);

        recordTransaction(wallet, WalletTransactionType.CREDIT, request.amount(),
            wallet.getTotalAvailable(), request.reference(), request.notes());

        log.info("Wallet top-up: company={} amount={}", companyId, request.amount());
        return WalletMapper.toResponse(wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WalletTransactionResponse> getTransactions(WalletTransactionType type, Pageable pageable) {
        Long companyId = requireCompanyId();
        Page<WalletTransaction> page = type != null
            ? txRepository.findByCompanyIdAndTypeOrderByTransactedAtDesc(companyId, type, pageable)
            : txRepository.findByCompanyIdOrderByTransactedAtDesc(companyId, pageable);
        return page.map(WalletMapper::toTransactionResponse);
    }

    @Override
    @Transactional
    public Wallet debit(Long companyId, BigDecimal amount, String reference, String notes) {
        Wallet wallet = walletRepository.findByCompanyId(companyId)
            .orElseThrow(() -> new BadRequestException("Wallet not found for company: " + companyId));

        if (wallet.getTotalAvailable().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient wallet balance");
        }

        // Deduct from credit balance first, then cash balance
        BigDecimal creditUsed = wallet.getCreditBalance().min(amount);
        BigDecimal cashUsed   = amount.subtract(creditUsed);

        wallet.setCreditBalance(wallet.getCreditBalance().subtract(creditUsed));
        wallet.setBalance(wallet.getBalance().subtract(cashUsed));
        walletRepository.save(wallet);

        recordTransaction(wallet, WalletTransactionType.DEBIT, amount,
            wallet.getTotalAvailable(), reference, notes);

        return wallet;
    }

    @Override
    @Transactional
    public Wallet credit(Long companyId, BigDecimal amount, WalletTransactionType type,
                          String reference, String notes) {
        Wallet wallet = walletRepository.findByCompanyId(companyId)
            .orElseGet(() -> createWallet(companyId));

        if (type == WalletTransactionType.CREDIT_APPLIED
                || type == WalletTransactionType.REFUND_CREDIT
                || type == WalletTransactionType.REFERRAL_REWARD) {
            wallet.setCreditBalance(wallet.getCreditBalance().add(amount));
        } else {
            wallet.setBalance(wallet.getBalance().add(amount));
        }
        walletRepository.save(wallet);

        recordTransaction(wallet, type, amount, wallet.getTotalAvailable(), reference, notes);
        return wallet;
    }

    // ── Private helpers ───────────────────────────────────────────

    private Wallet createWallet(Long companyId) {
        Company company = new Company();
        company.setId(companyId);
        Wallet w = Wallet.builder().company(company).build();
        return walletRepository.save(w);
    }

    private void recordTransaction(Wallet wallet, WalletTransactionType type,
                                    BigDecimal amount, BigDecimal balanceAfter,
                                    String reference, String notes) {
        Company company = new Company();
        company.setId(wallet.getCompany().getId());
        WalletTransaction tx = WalletTransaction.builder()
            .wallet(wallet)
            .company(company)
            .type(type)
            .amount(amount)
            .balanceAfter(balanceAfter)
            .reference(reference)
            .notes(notes)
            .transactedAt(LocalDateTime.now())
            .build();
        txRepository.save(tx);
    }

    private Long requireCompanyId() {
        Long id = securityUtil.getCurrentCompanyId();
        if (id == null) throw new BadRequestException("No company context");
        return id;
    }
}
