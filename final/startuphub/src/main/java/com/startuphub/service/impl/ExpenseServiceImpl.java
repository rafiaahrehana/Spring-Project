package com.startuphub.service.impl;

import com.startuphub.dto.request.ExpenseRequest;
import com.startuphub.dto.response.ExpenseResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.Employee;
import com.startuphub.entity.Expense;
import com.startuphub.enums.ExpenseStatus;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.HrmMapper;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.repository.ExpenseRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository  expenseRepository;
    private final EmployeeRepository employeeRepository;
    private final SecurityUtil       securityUtil;

    @Override
    @Transactional
    public ExpenseResponse submit(ExpenseRequest request) {
        Long companyId = requireCompanyId();
        Employee submitter = employeeRepository.findByUserId(securityUtil.getCurrentUser().getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));

        Expense expense = Expense.builder()
            .title(request.title())
            .category(request.category())
            .amount(request.amount())
            .expenseDate(request.expenseDate())
            .description(request.description())
            .receiptUrl(request.receiptUrl())
            .status(ExpenseStatus.PENDING)
            .submittedBy(submitter)
            .company(companyRef(companyId))
            .build();

        expenseRepository.save(expense);
        return HrmMapper.toExpenseResponse(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getById(Long id) {
        return HrmMapper.toExpenseResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> listAll(ExpenseStatus status, Pageable pageable) {
        Long companyId = requireCompanyId();
        Page<Expense> page = status != null
            ? expenseRepository.findByCompanyIdAndStatus(companyId, status, pageable)
            : expenseRepository.findByCompanyId(companyId, pageable);
        return page.map(HrmMapper::toExpenseResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> listMine(Pageable pageable) {
        Long companyId = requireCompanyId();
        Employee emp = employeeRepository.findByUserId(securityUtil.getCurrentUser().getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));
        return expenseRepository.findByCompanyIdAndSubmittedById(companyId, emp.getId(), pageable)
            .map(HrmMapper::toExpenseResponse);
    }

    @Override
    @Transactional
    public ExpenseResponse approve(Long id) {
        Expense expense = findInTenant(id);
        guardPending(expense);
        Employee approver = employeeRepository.findByUserId(securityUtil.getCurrentUser().getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));
        expense.setStatus(ExpenseStatus.APPROVED);
        expense.setApprovedBy(approver);
        return HrmMapper.toExpenseResponse(expense);
    }

    @Override
    @Transactional
    public ExpenseResponse reject(Long id, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new BadRequestException("Rejection reason is required");
        }
        Expense expense = findInTenant(id);
        guardPending(expense);
        Employee approver = employeeRepository.findByUserId(securityUtil.getCurrentUser().getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));
        expense.setStatus(ExpenseStatus.REJECTED);
        expense.setRejectionReason(reason);
        expense.setApprovedBy(approver);
        return HrmMapper.toExpenseResponse(expense);
    }

    @Override
    @Transactional
    public ExpenseResponse markReimbursed(Long id) {
        Expense expense = findInTenant(id);
        if (expense.getStatus() != ExpenseStatus.APPROVED) {
            throw new BadRequestException("Only APPROVED expenses can be reimbursed");
        }
        expense.setStatus(ExpenseStatus.REIMBURSED);
        expense.setReimbursedAt(LocalDateTime.now());
        return HrmMapper.toExpenseResponse(expense);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Expense expense = findInTenant(id);
        if (expense.getStatus() != ExpenseStatus.PENDING) {
            throw new BadRequestException("Only PENDING expenses can be deleted");
        }
        expense.softDelete();
    }

    private void guardPending(Expense expense) {
        if (expense.getStatus() != ExpenseStatus.PENDING) {
            throw new BadRequestException("Only PENDING expenses can be reviewed");
        }
    }

    private Expense findInTenant(Long id) {
        return expenseRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id));
    }

    private Long requireCompanyId() {
        Long id = securityUtil.getCurrentCompanyId();
        if (id == null) throw new BadRequestException("No company context");
        return id;
    }

    private Company companyRef(Long companyId) {
        Company c = new Company(); c.setId(companyId); return c;
    }
}
