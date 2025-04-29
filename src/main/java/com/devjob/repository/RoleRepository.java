package com.devjob.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devjob.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    boolean existsByName(String name);

    Optional<Role> findByName(String name);

}
