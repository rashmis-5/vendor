package com.buildsmart.vendor.service;

import com.buildsmart.vendor.dto.ProjectDTO;

import java.util.List;

public interface ProjectService {

    List<ProjectDTO> getAllProjects();

    ProjectDTO getProjectById(String id);

    List<ProjectDTO> searchByName(String projectName);

    ProjectDTO createProject(ProjectDTO dto);

    ProjectDTO updateProject(String id, ProjectDTO dto);

    void deleteProject(String id);
}

