package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.PromoCodeResponseDTO;
import com.StartupSAAS.entity.PromoCode;

public class PromoCodeMapper {

    public static PromoCodeResponseDTO toDTO(PromoCode promo) {

        PromoCodeResponseDTO dto = new PromoCodeResponseDTO();
        dto.setId(promo.getId());
        dto.setCode(promo.getCode());
        dto.setDiscountType(promo.getDiscountType());
        dto.setDiscountValue(promo.getDiscountValue());
        dto.setMinOrderAmount(promo.getMinOrderAmount());
        dto.setMaxUses(promo.getMaxUses());
        dto.setUsedCount(promo.getUsedCount());
        dto.setValidFrom(promo.getValidFrom());
        dto.setValidUntil(promo.getValidUntil());
        dto.setActive(promo.getActive());

        if (promo.getCompany() != null) {
            dto.setCompanyId(promo.getCompany().getId());
            dto.setCompanyName(promo.getCompany().getName());
        }

        return dto;
    }
}
