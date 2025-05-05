package com.devjob.service;

import com.devjob.dto.request.company.CompanyCreationRequest;
import com.devjob.dto.request.company.CompanyUpdateRequest;
import com.devjob.dto.response.common.PageResponse;
import com.devjob.dto.response.company.CompanyCreationResponse;
import com.devjob.dto.response.company.CompanyUpdateResponse;
import com.devjob.model.Company;

public interface CompanyService {
    PageResponse<Company> getAllCompanies(int page, int size);

    Company getCompanyById(Long companyId);

    CompanyCreationResponse createCompany(CompanyCreationRequest request);

    CompanyUpdateResponse updateCompany(Long companyId, CompanyUpdateRequest request);

    void deleteCompany(Long companyId);
}
