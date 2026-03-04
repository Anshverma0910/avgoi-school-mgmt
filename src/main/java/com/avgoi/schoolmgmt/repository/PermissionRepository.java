package com.avgoi.schoolmgmt.repository;

import com.avgoi.schoolmgmt.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
