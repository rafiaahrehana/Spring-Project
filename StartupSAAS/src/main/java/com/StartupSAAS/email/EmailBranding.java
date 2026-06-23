package com.StartupSAAS.email;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailBranding {

    private String companyName;
    private String logoUrl;
    private String primaryColor;

}