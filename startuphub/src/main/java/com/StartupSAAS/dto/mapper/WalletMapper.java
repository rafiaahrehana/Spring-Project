package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.WalletResponseDTO;
import com.StartupSAAS.entity.Client;
import com.StartupSAAS.entity.Wallet;

public class WalletMapper {

    public static WalletResponseDTO toDTO(Wallet wallet) {

        WalletResponseDTO dto = new WalletResponseDTO();
        dto.setId(wallet.getId());
        dto.setBalance(wallet.getBalance());

        Client client = wallet.getClient();
        if (client != null) {
            dto.setClientId(client.getId());
            if (client.getUser() != null) {
                dto.setClientFirstName(client.getUser().getFirstName());
                dto.setClientLastName(client.getUser().getLastName());
                dto.setClientEmail(client.getUser().getEmail());
            }
        }

        return dto;
    }
}
