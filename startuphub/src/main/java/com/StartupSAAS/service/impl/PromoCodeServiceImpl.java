package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.PromoCodeMapper;
import com.StartupSAAS.dto.request.PromoCodeRequestDTO;
import com.StartupSAAS.dto.response.PromoCodeResponseDTO;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.PromoCode;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.PromoCodeRepository;
import com.StartupSAAS.service.PromoCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromoCodeServiceImpl implements PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    @Override
    public PromoCodeResponseDTO create(PromoCodeRequestDTO dto) {

        if (promoCodeRepository.findByCode(dto.getCode().toUpperCase().trim()).isPresent()) {
            throw new RuntimeException("Promo code already exists: " + dto.getCode());
        }

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + dto.getCompanyId()));

        PromoCode promo = new PromoCode();
        promo.setCode(dto.getCode().toUpperCase().trim());
        promo.setDiscountType(dto.getDiscountType());
        promo.setDiscountValue(dto.getDiscountValue());
        promo.setMinOrderAmount(dto.getMinOrderAmount());
        promo.setMaxUses(dto.getMaxUses());
        promo.setValidFrom(dto.getValidFrom());
        promo.setValidUntil(dto.getValidUntil());
        promo.setUsedCount(0);
        promo.setActive(true);
        promo.setCompany(company);

        return PromoCodeMapper.toDTO(promoCodeRepository.save(promo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoCodeResponseDTO> getByCompany(Long companyId) {
        return promoCodeRepository.findByCompanyId(companyId)
                .stream().map(PromoCodeMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoCodeResponseDTO> getActiveByCompany(Long companyId) {
        return promoCodeRepository.findByCompanyIdAndActiveTrue(companyId)
                .stream().map(PromoCodeMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PromoCodeResponseDTO getByCode(String code) {
        return PromoCodeMapper.toDTO(
                promoCodeRepository.findByCode(code.toUpperCase().trim())
                        .orElseThrow(() -> new RuntimeException(
                                "Promo code not found: " + code)));
    }

    @Transactional
    @Override
    public PromoCodeResponseDTO validate(String code, Double orderAmount) {
        PromoCode promo = promoCodeRepository.findByCode(code.toUpperCase().trim())
                .orElseThrow(() -> new RuntimeException(
                        "Promo code not found: " + code));

        if (!promo.getActive())
            throw new RuntimeException("Promo code is inactive");

        LocalDate today = LocalDate.now();
        if (promo.getValidFrom() != null && today.isBefore(promo.getValidFrom()))
            throw new RuntimeException("Promo code not yet valid");

        if (promo.getValidUntil() != null && today.isAfter(promo.getValidUntil()))
            throw new RuntimeException("Promo code has expired");

        if (promo.getMaxUses() != null && promo.getUsedCount() >= promo.getMaxUses())
            throw new RuntimeException("Promo code usage limit reached");

        if (promo.getMinOrderAmount() != null && orderAmount < promo.getMinOrderAmount())
            throw new RuntimeException("Order amount below minimum for this promo code");

        // Increment usage
        promo.setUsedCount(promo.getUsedCount() + 1);
        return PromoCodeMapper.toDTO(promoCodeRepository.save(promo));
    }

    @Transactional
    @Override
    public PromoCodeResponseDTO update(Long id, PromoCodeRequestDTO dto) {
        PromoCode promo = promoCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "PromoCode not found with id: " + id));

        if (dto.getDiscountValue() != null)  promo.setDiscountValue(dto.getDiscountValue());
        if (dto.getDiscountType() != null)   promo.setDiscountType(dto.getDiscountType());
        if (dto.getMinOrderAmount() != null) promo.setMinOrderAmount(dto.getMinOrderAmount());
        if (dto.getMaxUses() != null)        promo.setMaxUses(dto.getMaxUses());
        if (dto.getValidFrom() != null)      promo.setValidFrom(dto.getValidFrom());
        if (dto.getValidUntil() != null)     promo.setValidUntil(dto.getValidUntil());

        return PromoCodeMapper.toDTO(promoCodeRepository.save(promo));
    }

    @Override
    public void delete(Long id) {
        promoCodeRepository.deleteById(id);
    }
}
