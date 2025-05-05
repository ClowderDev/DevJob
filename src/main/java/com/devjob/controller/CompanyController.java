package com.devjob.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devjob.dto.request.company.CompanyCreationRequest;
import com.devjob.dto.request.company.CompanyUpdateRequest;
import com.devjob.dto.response.common.PageResponse;
import com.devjob.dto.response.common.ResponseData;
import com.devjob.dto.response.company.CompanyCreationResponse;
import com.devjob.dto.response.company.CompanyUpdateResponse;
import com.devjob.model.Company;
import com.devjob.service.CompanyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping({ "", "/" })
    public ResponseData<PageResponse<Company>> getAllCompanies(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        var result = companyService.getAllCompanies(page, size);
        return ResponseData.<PageResponse<Company>>builder()
                .data(result)
                .message("Companies retrieved successfully")
                .build();
    }

    @GetMapping("/{companyId}")
    public ResponseData<Company> getCompanyById(@PathVariable(name = "companyId") Long companyId) {
        Company company = companyService.getCompanyById(companyId);
        return ResponseData.<Company>builder()
                .data(company)
                .message("Company retrieved successfully")
                .build();
    }

    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/create")
    public ResponseData<CompanyCreationResponse> createCompany(@RequestBody @Valid CompanyCreationRequest request) {
        log.info("Create company: {}", request);
        var result = companyService.createCompany(request);

        return ResponseData.<CompanyCreationResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Company created successfully")
                .data(result)
                .build();
    }

    @PreAuthorize("hasRole('STAFF')")
    @PutMapping("/update/{companyId}")
    public ResponseData<CompanyUpdateResponse> updateCompany(@PathVariable(name = "companyId") Long companyId,
            @RequestBody @Valid CompanyUpdateRequest request) {
        log.info("Update company: {}", request);
        var result = companyService.updateCompany(companyId, request);
        return ResponseData.<CompanyUpdateResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Company updated successfully")
                .data(result)
                .build();
    }

    @PreAuthorize("hasRole('STAFF')")
    @DeleteMapping("/delete/{companyId}")
    public ResponseData<Void> deleteCompany(@PathVariable(name = "companyId") Long companyId) {
        log.info("Delete company: {}", companyId);
        companyService.deleteCompany(companyId);
        return ResponseData.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Company deleted successfully")
                .build();
    }

}
