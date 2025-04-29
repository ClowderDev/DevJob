package com.devjob.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devjob.model.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT DISTINCT j FROM Job j " +
            "LEFT JOIN j.skills s " +
            "LEFT JOIN j.company c " +
            "WHERE LOWER(j.title) LIKE :keyword " +
            "OR LOWER(s.name) LIKE :keyword " +
            "OR LOWER(c.name) LIKE :keyword")
    Page<Job> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Job> findAll(Pageable pageable);

}
