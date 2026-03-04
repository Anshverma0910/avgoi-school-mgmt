package com.avgoi.schoolmgmt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RoleRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String tenantId;

    @NotNull
    private List<Long> permissionIds;
}
