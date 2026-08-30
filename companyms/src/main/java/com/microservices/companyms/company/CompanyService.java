package com.microservices.companyms.company;

import com.microservices.companyms.company.DTO.ReviewMessage;

import java.util.List;


public interface CompanyService {
    List<Company> getAllCompanies();
    boolean updateCompany(Company company,Long id);
    void createCompany(Company company);
    boolean deleteCompany(Long id);
    Company getCompanyById(Long id);
    void updateCompanyRating(ReviewMessage reviewMessage);
}
