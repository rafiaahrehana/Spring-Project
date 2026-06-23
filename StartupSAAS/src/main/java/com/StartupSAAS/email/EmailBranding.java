package com.StartupSAAS.email;

import com.StartupSAAS.entity.Company;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailBranding {

    @Value("${app.base-url}")
    private String baseUrl;

    public Data from(Company company) {
        String logoUrl = (company.getLogo() != null)
                ? baseUrl + "/images/company/" + company.getLogo()
                : null;

        return Data.builder()
                .companyName(company.getCompanyName())
                .logoUrl(logoUrl)
                .primaryColor(company.getPrimaryColor())
                .build();
    }

    @Getter
    @Builder
    public static class Data {
        private String companyName;
        private String logoUrl;
        private String primaryColor;
    }
}