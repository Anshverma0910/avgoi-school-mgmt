package com.avgoi.schoolmgmt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Base for tenant-scoped entities with yearly versioning (Student, Teacher, Grade, etc.).
 * Subclasses inherit the academicYear field.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class AbstractTenantEntity {

    @Column(name = "academic_year")
    private Integer academicYear;
}
