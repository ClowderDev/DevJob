package com.devjob.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devjob.dto.response.PageResponse;
import com.devjob.dto.response.ResponseData;
import com.devjob.model.Company;
import com.devjob.service.CompanyService;

import lombok.RequiredArgsConstructor;

@RestController
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

}
