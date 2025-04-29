package com.devjob.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devjob.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Page<Company> findAll(Pageable pageable);

    Optional<Company> findById(Long companyId);

}
