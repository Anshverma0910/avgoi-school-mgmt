package com.avgoi.schoolmgmt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSchoolRequest {

    @NotBlank(message = "schoolName is required")
    private String schoolName;

    @NotBlank(message = "registrationId is required")
    private String registrationId;

    private String city;
    private String address;
    private String contactNumber;
    private Map<String, Object> settings;
}
