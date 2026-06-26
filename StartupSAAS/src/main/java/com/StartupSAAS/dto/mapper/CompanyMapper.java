package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.request.CompanyRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.enums.SubscriptionPlan;
import com.StartupSAAS.location.mapper.AddressMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

  public User toUser(CompanyRequest request, PasswordEncoder encoder) {
    User user = new User();
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmail(request.getEmail());
    user.setPhone(request.getPhone());
    user.setPassword(encoder.encode(request.getPassword()));
    user.setRole(Role.COMPANY_OWNER);
    user.setActive(false);
    return user;
  }

  public Company toCompany(CompanyRequest request) {
    Company company = new Company();
    company.setCompanyName(request.getCompanyName());
    company.setCompanyEmail(request.getCompanyEmail());
    company.setCompanyPhone(request.getCompanyPhone());
    company.setSubdomain(request.getSubdomain());
    company.setWebsite(request.getWebsite());
    company.setSubscriptionPlan(
            request.getSubscriptionPlan() != null
                    ? request.getSubscriptionPlan()
                    : SubscriptionPlan.FREE);
    return company;
  }

  public CompanyResponse toDTO(Company company) {
    CompanyResponse response = new CompanyResponse();
    response.setId(company.getId());
    response.setCompanyName(company.getCompanyName());
    response.setCompanyEmail(company.getCompanyEmail());
    response.setCompanyPhone(company.getCompanyPhone());
    response.setSubdomain(company.getSubdomain());
    response.setLogo(company.getLogo());
    response.setWebsite(company.getWebsite());
    response.setSubscriptionPlan(company.getSubscriptionPlan());

    if (company.getUser() != null) {
      response.setOwnerId(company.getUser().getId());
      response.setOwnerName(company.getUser().getFirstName() + " " + company.getUser().getLastName());

      if (company.getUser().getAddress() != null)
        response.setAddress(AddressMapper.toDTO(company.getUser().getAddress()));
    }

    return response;
  }
}