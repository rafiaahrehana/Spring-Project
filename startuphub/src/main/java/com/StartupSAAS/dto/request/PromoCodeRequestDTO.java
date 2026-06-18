package com.StartupSAAS.dto.request;

import com.StartupSAAS.enums.DiscountType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PromoCodeRequestDTO {
    private String code;
    private DiscountType discountType;
    private Double discountValue;
    private Double minOrderAmount;
    private Integer maxUses;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private Long companyId;
}
