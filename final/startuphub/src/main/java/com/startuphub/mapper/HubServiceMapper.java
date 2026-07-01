package com.startuphub.mapper;

import com.startuphub.dto.response.HubServiceResponse;
import com.startuphub.entity.HubService;
import com.startuphub.entity.ServiceCategory;
import com.startuphub.entity.WorkflowTemplate;

public final class HubServiceMapper {

    private HubServiceMapper() {}

    public static HubServiceResponse toResponse(HubService s) {
        ServiceCategory cat = s.getCategory();
        WorkflowTemplate wf  = s.getWorkflowTemplate();
        return new HubServiceResponse(
            s.getId(),
            s.getName(),
            s.getNameBn(),
            s.getDescription(),
            s.getDescriptionBn(),
            s.getPrice(),
            s.getPriceType(),
            s.getEstimatedDays(),
            s.getDefaultPriority(),
            s.isActive(),
            cat != null ? cat.getId()   : null,
            cat != null ? cat.getName() : null,
            wf  != null ? wf.getId()    : null,
            wf  != null ? wf.getName()  : null,
            s.getCreatedAt()
        );
    }
}
