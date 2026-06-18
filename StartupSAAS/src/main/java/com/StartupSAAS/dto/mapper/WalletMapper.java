package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.WalletResponse;
import com.StartupSAAS.entity.Wallet;

public class WalletMapper {

    public static WalletResponse toDTO(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.setId(wallet.getId());
        response.setBalance(wallet.getBalance());

        if (wallet.getCompany() != null) {
            response.setCompanyId(wallet.getCompany().getId());
            response.setCompanyName(wallet.getCompany().getCompanyName());
        }

        return response;
    }
}