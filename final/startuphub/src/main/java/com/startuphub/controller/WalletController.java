package com.startuphub.controller;

import com.startuphub.dto.request.WalletTopUpRequest;
import com.startuphub.dto.response.ApiResponse;
import com.startuphub.dto.response.WalletResponse;
import com.startuphub.dto.response.WalletTransactionResponse;
import com.startuphub.enums.WalletTransactionType;
import com.startuphub.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet", description = "Company wallet balance and transaction history")
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Get current wallet balance")
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet() {
        return ResponseEntity.ok(ApiResponse.success(walletService.getOrCreateWallet()));
    }

    @PostMapping("/top-up")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Top up wallet — SUPER_ADMIN only (triggered by payment confirmation)")
    public ResponseEntity<ApiResponse<WalletResponse>> topUp(
            @Valid @RequestBody WalletTopUpRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Wallet topped up",
            walletService.topUp(request)));
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'ADMIN')")
    @Operation(summary = "Get transaction history")
    public ResponseEntity<ApiResponse<Page<WalletTransactionResponse>>> getTransactions(
            @RequestParam(required = false) WalletTransactionType type,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            walletService.getTransactions(type,
                PageRequest.of(page, size, Sort.by("transactedAt").descending()))));
    }
}
