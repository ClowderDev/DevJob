package com.devjob.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.devjob.dto.request.company.CompanyCreationRequest;
import com.devjob.dto.request.company.CompanyUpdateRequest;
import com.devjob.dto.response.common.PageResponse;
import com.devjob.dto.response.company.CompanyCreationResponse;
import com.devjob.dto.response.company.CompanyUpdateResponse;
import com.devjob.exception.AppException;
import com.devjob.exception.ErrorCode;
import com.devjob.model.Company;
import com.devjob.repository.CompanyRepository;
import com.devjob.service.CompanyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public PageResponse<Company> getAllCompanies(int page, int size) {
        int pageNumber = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(pageNumber, size);

        Page<Company> companyPage = companyRepository.findAll(pageable);

        return PageResponse.<Company>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(companyPage.getTotalPages())
                .totalElements(companyPage.getTotalElements())
                .data(companyPage.getContent())
                .build();
    }

    public Company getCompanyById(Long companyId) {
        return companyRepository.findById(companyId).orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_EXISTED));
    }

    public CompanyCreationResponse createCompany(CompanyCreationRequest request) {
        log.info("Company creation started");
        if (companyRepository.existsByName(request.getName())) {
            log.error("Company exist with name: {}", request.getName());
            throw new AppException(ErrorCode.COMPANY_EXISTED);
        }

        Company company = Company.builder()
                .name(request.getName())
                .address(request.getAddress())
                .companySize(request.getCompanySize())
                .description(request.getDescription())
                .build();

        companyRepository.save(company);
        log.info("Company created");
        return CompanyCreationResponse.builder()
                .name(request.getName())
                .address(request.getAddress())
                .companySize(request.getCompanySize())
                .description(request.getDescription())
                .build();
    }

    public CompanyUpdateResponse updateCompany(Long companyId, CompanyUpdateRequest request) {
        log.info("Company update started");

        if (!companyRepository.existsById(companyId)) {
            log.error("Company not found with id: {}", companyId);
            throw new AppException(ErrorCode.COMPANY_NOT_EXISTED);
        }

        Company company = getCompanyById(companyId);
        company.setName(request.getName());
        company.setAddress(request.getAddress());
        company.setCompanySize(request.getCompanySize());
        company.setDescription(request.getDescription());
        companyRepository.save(company);
        log.info("Company updated");
        return CompanyUpdateResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .address(company.getAddress())
                .companySize(company.getCompanySize())
                .description(company.getDescription())
                .build();
    }

    public void deleteCompany(Long companyId) {
        log.info("Company delete started");
        if (!companyRepository.existsById(companyId)) {
            log.error("Company not found with id: {}", companyId);
            throw new AppException(ErrorCode.COMPANY_NOT_EXISTED);
        }
        companyRepository.deleteById(companyId);
        log.info("Company deleted");
    }

}
