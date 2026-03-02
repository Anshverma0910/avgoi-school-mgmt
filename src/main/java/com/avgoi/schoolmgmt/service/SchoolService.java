package com.avgoi.schoolmgmt.service;

import com.avgoi.schoolmgmt.dto.CreateSchoolRequest;
import com.avgoi.schoolmgmt.dto.UpdateSchoolRequest;
import com.avgoi.schoolmgmt.entity.School;
import com.avgoi.schoolmgmt.repository.SchoolRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for school registration and management (AVGOI).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Registers a new school. Fails if registrationId is already in use.
     */
    @Transactional
    public School register(CreateSchoolRequest request) {
        if (schoolRepository.existsByRegistrationId(request.getRegistrationId())) {
            throw new DuplicateRegistrationException(
                    "School with registration ID already exists: " + request.getRegistrationId());
        }
        School school = new School();
        school.setSchoolName(request.getSchoolName());
        school.setRegistrationId(request.getRegistrationId());
        school.setCity(request.getCity());
        school.setAddress(request.getAddress());
        school.setContactNumber(request.getContactNumber());
        school.setSettings(request.getSettings());
        School saved = createSchemaIfNotExistsAndSave(school);
        log.info("Created isolated schema for tenant: {}", request.getRegistrationId());
        return saved;
    }

    /**
     * Ensures tenant schema exists (same transaction as save) then saves the school.
     * Schema name is cleaned to alphanumeric and underscores only for PostgreSQL.
     */
    private School createSchemaIfNotExistsAndSave(School school) {
        String schemaName = toSchemaName(school.getRegistrationId());
        if (!schemaName.isBlank()) {
            jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
        }
        return schoolRepository.save(school);
    }

    /**
     * Cleans registrationId to only alphanumeric and underscore (safe for PostgreSQL schema names).
     */
    private static String toSchemaName(String registrationId) {
        if (registrationId == null || registrationId.isBlank()) {
            return "";
        }
        return registrationId.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    public School getSchoolById(Long id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("School not found with id: " + id));
    }

    public List<School> getSchoolsByCity(String city) {
        return schoolRepository.findByCity(city);
    }

    @Transactional
    public School updateSchool(Long id, UpdateSchoolRequest request) {
        School school = getSchoolById(id);
        if (request.getSchoolName() != null) {
            school.setSchoolName(request.getSchoolName());
        }
        if (request.getRegistrationId() != null) {
            if (schoolRepository.existsByRegistrationId(request.getRegistrationId())
                    && !request.getRegistrationId().equals(school.getRegistrationId())) {
                throw new DuplicateRegistrationException(
                        "School with registration ID already exists: " + request.getRegistrationId());
            }
            school.setRegistrationId(request.getRegistrationId());
        }
        if (request.getCity() != null) {
            school.setCity(request.getCity());
        }
        if (request.getAddress() != null) {
            school.setAddress(request.getAddress());
        }
        if (request.getContactNumber() != null) {
            school.setContactNumber(request.getContactNumber());
        }
        if (request.getSettings() != null && !request.getSettings().isEmpty()) {
            Map<String, Object> merged = school.getSettings() != null
                    ? new HashMap<>(school.getSettings())
                    : new HashMap<>();
            merged.putAll(request.getSettings());
            school.setSettings(merged);
        }
        School saved = createSchemaIfNotExistsAndSave(school);
        log.info("Created isolated schema for tenant: {}", school.getRegistrationId());
        return saved;
    }

    @Transactional
    public void deleteSchool(Long id) {
        if (!schoolRepository.existsById(id)) {
            throw new EntityNotFoundException("School not found with id: " + id);
        }
        schoolRepository.deleteById(id);
    }
}
