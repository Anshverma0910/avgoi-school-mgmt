package com.avgoi.schoolmgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSchoolRequest {

    private String schoolName;
    private String registrationId;
    private String city;
    private String address;
    private String contactNumber;
    private Map<String, Object> settings;
}
