package com.buildsmart.vendor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @Column(name = "project_id", nullable = false, updatable = false, length = 20)
    private String projectId;

    @Column(nullable = false, unique = true, length = 200)
    private String projectName;

    @Column(length = 1000)
    private String description;
}

