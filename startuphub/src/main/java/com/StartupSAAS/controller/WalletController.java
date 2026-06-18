package com.StartupSAAS.controller;

import com.StartupSAAS.dto.response.WalletResponseDTO;
import com.StartupSAAS.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/client/{clientId}")
    public WalletResponseDTO getByClient(@PathVariable Long clientId) {
        return walletService.getByClient(clientId);
    }

    // POST /api/wallets/client/3/credit?amount=500&reference=Refund
    @PostMapping("/client/{clientId}/credit")
    public WalletResponseDTO credit(
            @PathVariable Long clientId,
            @RequestParam Double amount,
            @RequestParam(required = false) String reference) {
        return walletService.credit(clientId, amount,
                reference != null ? reference : "Manual credit");
    }

    // POST /api/wallets/client/3/debit?amount=200&reference=Payment
    @PostMapping("/client/{clientId}/debit")
    public WalletResponseDTO debit(
            @PathVariable Long clientId,
            @RequestParam Double amount,
            @RequestParam(required = false) String reference) {
        return walletService.debit(clientId, amount,
                reference != null ? reference : "Manual debit");
    }
}
