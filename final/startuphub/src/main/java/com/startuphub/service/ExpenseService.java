package com.startuphub.service;

import com.startuphub.dto.request.ExpenseRequest;
import com.startuphub.dto.response.ExpenseResponse;
import com.startuphub.enums.ExpenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpenseService {
    ExpenseResponse submit(ExpenseRequest request);
    ExpenseResponse getById(Long id);
    Page<ExpenseResponse> listAll(ExpenseStatus status, Pageable pageable);
    Page<ExpenseResponse> listMine(Pageable pageable);
    ExpenseResponse approve(Long id);
    ExpenseResponse reject(Long id, String reason);
    ExpenseResponse markReimbursed(Long id);
    void delete(Long id);
}
