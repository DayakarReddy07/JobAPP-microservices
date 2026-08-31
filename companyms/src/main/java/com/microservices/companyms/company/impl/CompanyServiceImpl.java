package com.microservices.companyms.company.impl;


import com.microservices.companyms.company.Company;
import com.microservices.companyms.company.CompanyRepository;
import com.microservices.companyms.company.CompanyService;
import com.microservices.companyms.company.DTO.ReviewMessage;
import com.microservices.companyms.company.clients.ReviewClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final ReviewClient reviewClient;

    public CompanyServiceImpl(CompanyRepository companyRepository, ReviewClient reviewClient) {
        this.companyRepository = companyRepository;
        this.reviewClient = reviewClient;
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public boolean updateCompany(Company company, Long id) {
        Optional<Company> optionalCompany = companyRepository.findById(id);
        if (optionalCompany.isPresent()) {
            Company existingCompany = optionalCompany.get();
            existingCompany.setName(company.getName());
            existingCompany.setDescription(company.getDescription());
            companyRepository.save(existingCompany);
            return true;
        }
        return false;
    }

    @Override
    public void createCompany(Company company) {
        companyRepository.save(company);
    }

    @Override
    public boolean deleteCompany(Long id) {
        if(companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Company getCompanyById(Long id) {
        return  companyRepository.findById(id).orElse(null);
    }

    @Override
    public void updateCompanyRating(ReviewMessage reviewMessage) {
        Company company = companyRepository.findById(reviewMessage.getCompanyId()).orElse(null);
        double averageRating = reviewClient.getAverageReview(reviewMessage.getCompanyId());
        company.setRating(averageRating);
        companyRepository.save(company);
    }

}
