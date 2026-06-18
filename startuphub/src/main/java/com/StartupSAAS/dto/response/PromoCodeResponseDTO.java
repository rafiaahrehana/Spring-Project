package com.StartupSAAS.dto.response;

import com.StartupSAAS.enums.DiscountType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PromoCodeResponseDTO {
    private Long id;
    private String code;
    private DiscountType discountType;
    private Double discountValue;
    private Double minOrderAmount;
    private Integer maxUses;
    private Integer usedCount;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private Boolean active;

    // Flattened company
    private Long companyId;
    private String companyName;
}
