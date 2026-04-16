package com.buildsmart.vendor.repository;

import com.buildsmart.vendor.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    Optional<Project> findTopByOrderByProjectIdDesc();

    Optional<Project> findByProjectNameIgnoreCase(String projectName);
}

