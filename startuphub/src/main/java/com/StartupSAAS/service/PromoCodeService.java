package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.PromoCodeRequestDTO;
import com.StartupSAAS.dto.response.PromoCodeResponseDTO;

import java.util.List;

public interface PromoCodeService {

    PromoCodeResponseDTO create(PromoCodeRequestDTO dto);
    List<PromoCodeResponseDTO> getByCompany(Long companyId);
    List<PromoCodeResponseDTO> getActiveByCompany(Long companyId);
    PromoCodeResponseDTO getByCode(String code);
    PromoCodeResponseDTO validate(String code, Double orderAmount);
    PromoCodeResponseDTO update(Long id, PromoCodeRequestDTO dto);
    void delete(Long id);
}
