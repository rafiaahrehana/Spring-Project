package com.startuphub.mapper;

import com.startuphub.dto.response.AssetResponse;
import com.startuphub.dto.response.AttendanceResponse;
import com.startuphub.dto.response.ExpenseResponse;
import com.startuphub.dto.response.LeaveBalanceResponse;
import com.startuphub.dto.response.LeaveRequestResponse;
import com.startuphub.dto.response.PayrollResponse;
import com.startuphub.dto.response.TimesheetResponse;
import com.startuphub.entity.Asset;
import com.startuphub.entity.Attendance;
import com.startuphub.entity.Employee;
import com.startuphub.entity.Expense;
import com.startuphub.entity.LeaveBalance;
import com.startuphub.entity.LeaveRequest;
import com.startuphub.entity.Payroll;
import com.startuphub.entity.Task;
import com.startuphub.entity.Timesheet;
import com.startuphub.entity.User;

public final class HrmMapper {

    private HrmMapper() {}

    public static AttendanceResponse toAttendanceResponse(Attendance a) {
        Employee emp = a.getEmployee();
        User empUser = emp != null ? emp.getUser() : null;
        return new AttendanceResponse(
            a.getId(), a.getDate(), a.getCheckIn(), a.getCheckOut(),
            a.isPresent(), a.getNotes(),
            emp != null ? emp.getId() : null,
            empUser != null ? empUser.getFullName() : null,
            a.getCreatedAt()
        );
    }

    public static LeaveRequestResponse toLeaveRequestResponse(LeaveRequest lr) {
        Employee emp = lr.getEmployee();
        User empUser = emp != null ? emp.getUser() : null;
        Employee reviewer = lr.getReviewedBy();
        User reviewerUser = reviewer != null ? reviewer.getUser() : null;
        return new LeaveRequestResponse(
            lr.getId(), lr.getLeaveType(), lr.getStartDate(), lr.getEndDate(),
            lr.getTotalDays(), lr.getReason(), lr.getStatus(), lr.getRejectionReason(),
            lr.getReviewedAt(),
            emp != null ? emp.getId() : null,
            empUser != null ? empUser.getFullName() : null,
            reviewer != null ? reviewer.getId() : null,
            reviewerUser != null ? reviewerUser.getFullName() : null,
            lr.getCreatedAt()
        );
    }

    public static LeaveBalanceResponse toLeaveBalanceResponse(LeaveBalance lb) {
        return new LeaveBalanceResponse(
            lb.getId(), lb.getLeaveType(), lb.getYear(),
            lb.getEntitledDays(), lb.getUsedDays(), lb.getPendingDays(),
            lb.getRemainingDays()
        );
    }

    public static PayrollResponse toPayrollResponse(Payroll p) {
        Employee emp = p.getEmployee();
        User empUser = emp != null ? emp.getUser() : null;
        Employee approver = p.getApprovedBy();
        User approverUser = approver != null ? approver.getUser() : null;
        return new PayrollResponse(
            p.getId(), p.getPayMonth(), p.getPayYear(),
            p.getBasicSalary(), p.getHouseRent(), p.getMedicalAllowance(),
            p.getTransportAllowance(), p.getBonus(), p.getDeductions(),
            p.getTaxDeduction(), p.getNetSalary(), p.getStatus(),
            p.getPaymentReference(), p.getPaidAt(), p.getNotes(),
            emp != null ? emp.getId() : null,
            empUser != null ? empUser.getFullName() : null,
            approver != null ? approver.getId() : null,
            approverUser != null ? approverUser.getFullName() : null,
            p.getCreatedAt()
        );
    }

    public static AssetResponse toAssetResponse(Asset a) {
        Employee assigned = a.getAssignedTo();
        User assignedUser = assigned != null ? assigned.getUser() : null;
        return new AssetResponse(
            a.getId(), a.getName(), a.getCategory(), a.getSerialNumber(),
            a.getDescription(), a.getPurchaseDate(), a.getPurchaseCost(),
            a.getStatus(), a.getAssignedAt(), a.getReturnDate(), a.getNotes(),
            assigned != null ? assigned.getId() : null,
            assignedUser != null ? assignedUser.getFullName() : null,
            a.getCreatedAt()
        );
    }

    public static ExpenseResponse toExpenseResponse(Expense e) {
        Employee sub = e.getSubmittedBy();
        User subUser = sub != null ? sub.getUser() : null;
        Employee approver = e.getApprovedBy();
        User approverUser = approver != null ? approver.getUser() : null;
        return new ExpenseResponse(
            e.getId(), e.getTitle(), e.getCategory(), e.getAmount(),
            e.getExpenseDate(), e.getDescription(), e.getReceiptUrl(),
            e.getStatus(), e.getRejectionReason(), e.getReimbursedAt(),
            sub != null ? sub.getId() : null,
            subUser != null ? subUser.getFullName() : null,
            approver != null ? approver.getId() : null,
            approverUser != null ? approverUser.getFullName() : null,
            e.getCreatedAt()
        );
    }

    public static TimesheetResponse toTimesheetResponse(Timesheet t) {
        Employee emp = t.getEmployee();
        User empUser = emp != null ? emp.getUser() : null;
        Employee approver = t.getApprovedBy();
        User approverUser = approver != null ? approver.getUser() : null;
        Task task = t.getTask();
        return new TimesheetResponse(
            t.getId(), t.getWorkDate(), t.getStartTime(), t.getEndTime(),
            t.getHoursWorked(), t.getBillableHours(), t.getDescription(),
            t.isApproved(), t.getApprovedAt(),
            emp != null ? emp.getId() : null,
            empUser != null ? empUser.getFullName() : null,
            approver != null ? approver.getId() : null,
            approverUser != null ? approverUser.getFullName() : null,
            task != null ? task.getId() : null,
            task != null ? task.getTitle() : null,
            t.getCreatedAt()
        );
    }
}
