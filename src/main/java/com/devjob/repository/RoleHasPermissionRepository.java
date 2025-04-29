package com.devjob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devjob.model.RoleHasPermission;

@Repository
public interface RoleHasPermissionRepository extends JpaRepository<RoleHasPermission, Integer> {
}