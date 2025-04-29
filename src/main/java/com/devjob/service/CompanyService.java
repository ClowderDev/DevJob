package com.devjob.service;

import com.devjob.dto.response.PageResponse;
import com.devjob.model.Company;

public interface CompanyService {
    PageResponse<Company> getAllCompanies(int page, int size);

    Company getCompanyById(Long companyId);
}
