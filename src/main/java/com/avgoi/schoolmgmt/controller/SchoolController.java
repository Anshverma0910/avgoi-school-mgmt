package com.avgoi.schoolmgmt.controller;

import com.avgoi.schoolmgmt.dto.CreateSchoolRequest;
import com.avgoi.schoolmgmt.dto.UpdateSchoolRequest;
import com.avgoi.schoolmgmt.entity.School;
import com.avgoi.schoolmgmt.service.SchoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for school management (AVGOI).
 */
@RestController
@RequestMapping("/api/schools")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @GetMapping
    public ResponseEntity<List<School>> getAllSchools() {
        return ResponseEntity.ok(schoolService.getAllSchools());
    }

    @GetMapping("/search")
    public ResponseEntity<List<School>> searchByCity(@RequestParam String city) {
        return ResponseEntity.ok(schoolService.getSchoolsByCity(city));
    }

    @GetMapping("/{id}")
    public ResponseEntity<School> getSchoolById(@PathVariable Long id) {
        return ResponseEntity.ok(schoolService.getSchoolById(id));
    }

    @PostMapping
    public ResponseEntity<School> createSchool(@Valid @RequestBody CreateSchoolRequest request) {
        School school = schoolService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(school);
    }

    @PutMapping("/{id}")
    public ResponseEntity<School> updateSchool(@PathVariable Long id,
                                               @RequestBody UpdateSchoolRequest request) {
        return ResponseEntity.ok(schoolService.updateSchool(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchool(@PathVariable Long id) {
        schoolService.deleteSchool(id);
        return ResponseEntity.noContent().build();
    }
}
