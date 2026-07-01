package com.startuphub.mapper;

import com.startuphub.dto.response.JobPostingResponse;
import com.startuphub.entity.Department;
import com.startuphub.entity.Employee;
import com.startuphub.entity.JobPosting;
import com.startuphub.entity.User;

public final class JobPostingMapper {

    private JobPostingMapper() {}

    public static JobPostingResponse toResponse(JobPosting j) {
        Department dept = j.getDepartment();
        Employee creator = j.getCreatedBy();
        User creatorUser = creator != null ? creator.getUser() : null;
        return new JobPostingResponse(
            j.getId(),
            j.getTitle(),
            j.getJobTitle(),
            j.getDescription(),
            j.getRequirements(),
            j.getEmploymentType(),
            j.getStatus(),
            j.getVacancies(),
            j.getSalaryMin(),
            j.getSalaryMax(),
            j.getDeadline(),
            j.isRemote(),
            dept != null ? dept.getId()   : null,
            dept != null ? dept.getName() : null,
            creator     != null ? creator.getId() : null,
            creatorUser != null ? creatorUser.getFullName() : null,
            j.getCreatedAt()
        );
    }
}
