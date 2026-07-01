package com.startuphub.service.impl;

import com.startuphub.dto.request.CreatePayrollRequest;
import com.startuphub.dto.response.PayrollResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.Employee;
import com.startuphub.entity.Payroll;
import com.startuphub.enums.PayrollStatus;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.HrmMapper;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.repository.PayrollRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.PayrollService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository  payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final SecurityUtil       securityUtil;

    @Override
    @Transactional
    public PayrollResponse create(CreatePayrollRequest request) {
        Long companyId = requireCompanyId();

        if (payrollRepository.findByEmployeeIdAndPayMonthAndPayYear(
                request.employeeId(), request.payMonth(), request.payYear()).isPresent()) {
            throw new BadRequestException("Payroll already exists for this employee and period");
        }

        Employee employee = employeeRepository.findByIdAndCompanyId(request.employeeId(), companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + request.employeeId()));

        BigDecimal basic    = request.basicSalary();
        BigDecimal rent     = orZero(request.houseRent());
        BigDecimal medical  = orZero(request.medicalAllowance());
        BigDecimal transport= orZero(request.transportAllowance());
        BigDecimal bonus    = orZero(request.bonus());
        BigDecimal deductions = orZero(request.deductions());
        BigDecimal tax      = orZero(request.taxDeduction());
        BigDecimal gross    = basic.add(rent).add(medical).add(transport).add(bonus);
        BigDecimal net      = gross.subtract(deductions).subtract(tax);

        Payroll payroll = Payroll.builder()
            .employee(employee)
            .company(companyRef(companyId))
            .payMonth(request.payMonth())
            .payYear(request.payYear())
            .basicSalary(basic)
            .houseRent(rent)
            .medicalAllowance(medical)
            .transportAllowance(transport)
            .bonus(bonus)
            .deductions(deductions)
            .taxDeduction(tax)
            .netSalary(net)
            .notes(request.notes())
            .status(PayrollStatus.DRAFT)
            .build();

        payrollRepository.save(payroll);
        log.info("Payroll created: employee={} {}/{}", request.employeeId(), request.payMonth(), request.payYear());
        return HrmMapper.toPayrollResponse(payroll);
    }

    @Override
    @Transactional(readOnly = true)
    public PayrollResponse getById(Long id) {
        return HrmMapper.toPayrollResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PayrollResponse> listByPeriod(int month, int year, Pageable pageable) {
        return payrollRepository.findByCompanyIdAndPayMonthAndPayYear(
                requireCompanyId(), month, year, pageable)
            .map(HrmMapper::toPayrollResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PayrollResponse> listForEmployee(Long employeeId, Pageable pageable) {
        return payrollRepository.findByCompanyIdAndEmployeeId(requireCompanyId(), employeeId, pageable)
            .map(HrmMapper::toPayrollResponse);
    }

    @Override
    @Transactional
    public PayrollResponse approve(Long id) {
        Payroll p = findInTenant(id);
        if (p.getStatus() != PayrollStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT payrolls can be approved");
        }
        Employee approver = employeeRepository.findByUserId(securityUtil.getCurrentUser().getId())
            .orElseThrow(() -> new BadRequestException("Employee profile not found"));
        p.setStatus(PayrollStatus.APPROVED);
        p.setApprovedBy(approver);
        return HrmMapper.toPayrollResponse(p);
    }

    @Override
    @Transactional
    public PayrollResponse markPaid(Long id, String paymentReference) {
        Payroll p = findInTenant(id);
        if (p.getStatus() != PayrollStatus.APPROVED) {
            throw new BadRequestException("Only APPROVED payrolls can be marked as paid");
        }
        p.setStatus(PayrollStatus.PAID);
        p.setPaymentReference(paymentReference);
        p.setPaidAt(LocalDate.now());
        return HrmMapper.toPayrollResponse(p);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Payroll p = findInTenant(id);
        if (p.getStatus() == PayrollStatus.PAID) {
            throw new BadRequestException("Cannot delete a paid payroll");
        }
        p.softDelete();
    }

    private Payroll findInTenant(Long id) {
        return payrollRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Payroll not found: " + id));
    }

    private Long requireCompanyId() {
        Long id = securityUtil.getCurrentCompanyId();
        if (id == null) throw new BadRequestException("No company context");
        return id;
    }

    private Company companyRef(Long companyId) {
        Company c = new Company(); c.setId(companyId); return c;
    }

    private BigDecimal orZero(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
