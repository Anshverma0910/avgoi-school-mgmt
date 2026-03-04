package com.avgoi.schoolmgmt.service;

import com.avgoi.schoolmgmt.dto.RoleRequest;
import com.avgoi.schoolmgmt.entity.Permission;
import com.avgoi.schoolmgmt.entity.Role;
import com.avgoi.schoolmgmt.repository.PermissionRepository;
import com.avgoi.schoolmgmt.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<Role> findByTenantId(String tenantId) {
        return roleRepository.findByTenantId(tenantId);
    }

    @Transactional(readOnly = true)
    public Role getById(Long id) {
        return roleRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + id));
    }

    @Transactional
    public Role save(RoleRequest request) {
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
        Role role = new Role();
        role.setName(request.getName());
        role.setTenantId(request.getTenantId());
        role.setPermissions(permissions);
        return roleRepository.save(role);
    }

    @Transactional
    public Role update(Long id, RoleRequest request) {
        Role role = roleRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + id));
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
        role.setName(request.getName());
        role.setTenantId(request.getTenantId());
        role.setPermissions(permissions);
        return roleRepository.save(role);
    }
}
