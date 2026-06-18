package com.StartupSAAS.controller;

import com.StartupSAAS.dto.request.PromoCodeRequestDTO;
import com.StartupSAAS.dto.response.PromoCodeResponseDTO;
import com.StartupSAAS.service.PromoCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promo-codes")
@RequiredArgsConstructor
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @PostMapping
    public ResponseEntity<PromoCodeResponseDTO> create(
            @RequestBody PromoCodeRequestDTO dto) {
        return new ResponseEntity<>(promoCodeService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<PromoCodeResponseDTO>> getByCompany(
            @PathVariable Long companyId) {
        List<PromoCodeResponseDTO> list = promoCodeService.getByCompany(companyId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/company/{companyId}/active")
    public ResponseEntity<List<PromoCodeResponseDTO>> getActiveByCompany(
            @PathVariable Long companyId) {
        List<PromoCodeResponseDTO> list = promoCodeService.getActiveByCompany(companyId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    // GET /api/promo-codes/LAUNCH50
    @GetMapping("/{code}")
    public PromoCodeResponseDTO getByCode(@PathVariable String code) {
        return promoCodeService.getByCode(code);
    }

    // POST /api/promo-codes/LAUNCH50/validate?orderAmount=1000
    @PostMapping("/{code}/validate")
    public PromoCodeResponseDTO validate(
            @PathVariable String code,
            @RequestParam Double orderAmount) {
        return promoCodeService.validate(code, orderAmount);
    }

    @PutMapping("/{id}")
    public PromoCodeResponseDTO update(
            @PathVariable Long id,
            @RequestBody PromoCodeRequestDTO dto) {
        return promoCodeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        promoCodeService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}
