package com.buildsmart.vendor.service.impl;

import com.buildsmart.vendor.dto.ProjectDTO;
import com.buildsmart.vendor.exception.DuplicateResourceException;
import com.buildsmart.vendor.exception.ResourceNotFoundException;
import com.buildsmart.vendor.model.Project;
import com.buildsmart.vendor.repository.ProjectRepository;
import com.buildsmart.vendor.service.ProjectService;
import com.buildsmart.vendor.util.IdGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;

    @Override
    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        List<ProjectDTO> result = new ArrayList<>();

        for (Project project : projects) {
            result.add(toDTO(project));
        }
        return result;
    }

    @Override
    public ProjectDTO getProjectById(String id) {
        return toDTO(findById(id));
    }

    @Override
    public List<ProjectDTO> searchByName(String projectName) {
        List<ProjectDTO> result = new ArrayList<>();
        projectRepository.findByProjectNameIgnoreCase(projectName.trim())
                .ifPresent(project -> result.add(toDTO(project)));
        return result;
    }

    @Override
    public ProjectDTO createProject(ProjectDTO dto) {
        log.info("Creating project, name={}", dto.getProjectName());
        validateInput(dto);

        projectRepository.findByProjectNameIgnoreCase(dto.getProjectName().trim())
                .ifPresent(project -> {
                    throw new DuplicateResourceException("Project name already exists");
                });

        String lastId = projectRepository.findTopByOrderByProjectIdDesc()
                .map(Project::getProjectId)
                .orElse(null);
        String newId = IdGeneratorUtil.nextProjectId(lastId);

        Project project = Project.builder()
                .projectId(newId)
                .projectName(dto.getProjectName().trim())
                .description(dto.getDescription())
                .build();

        return toDTO(projectRepository.save(project));
    }

    @Override
    public ProjectDTO updateProject(String id, ProjectDTO dto) {
        log.info("Updating project id={}", id);
        validateInput(dto);

        Project existing = findById(id);

        projectRepository.findByProjectNameIgnoreCase(dto.getProjectName().trim())
                .filter(project -> !project.getProjectId().equals(id))
                .ifPresent(project -> {
                    throw new DuplicateResourceException("Project name already exists");
                });

        existing.setProjectName(dto.getProjectName().trim());
        existing.setDescription(dto.getDescription());

        return toDTO(projectRepository.save(existing));
    }

    @Override
    public void deleteProject(String id) {
        log.info("Deleting project id={}", id);
        findById(id);
        projectRepository.deleteById(id);
    }

    private Project findById(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }

    private void validateInput(ProjectDTO dto) {
        if (dto.getProjectName() == null || dto.getProjectName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name is required");
        }
    }

    private ProjectDTO toDTO(Project project) {
        return ProjectDTO.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .description(project.getDescription())
                .build();
    }
}
