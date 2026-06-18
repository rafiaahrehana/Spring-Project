package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.WalletRequest;
import com.StartupSAAS.dto.response.WalletResponse;
import com.StartupSAAS.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<WalletResponse> getByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(walletService.getWalletByCompany(companyId));
    }

    @PatchMapping("/company/{companyId}/credit")
    public ResponseEntity<WalletResponse> credit(
            @PathVariable Long companyId,
            @RequestBody WalletRequest request) {
        return ResponseEntity.ok(walletService.creditWallet(companyId, request.getAmount()));
    }

    @PatchMapping("/company/{companyId}/debit")
    public ResponseEntity<WalletResponse> debit(
            @PathVariable Long companyId,
            @RequestBody WalletRequest request) {
        return ResponseEntity.ok(walletService.debitWallet(companyId, request.getAmount()));
    }
}
