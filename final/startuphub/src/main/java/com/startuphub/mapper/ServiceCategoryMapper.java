package com.startuphub.mapper;

import com.startuphub.dto.response.ServiceCategoryResponse;
import com.startuphub.entity.ServiceCategory;

public final class ServiceCategoryMapper {

    private ServiceCategoryMapper() {}

    public static ServiceCategoryResponse toResponse(ServiceCategory cat) {
        return new ServiceCategoryResponse(
            cat.getId(),
            cat.getName(),
            cat.getNameBn(),
            cat.getDescription(),
            cat.getIconUrl(),
            cat.getSortOrder(),
            cat.isActive()
        );
    }
}
