package com.avgoi.schoolmgmt.controller;

import com.avgoi.schoolmgmt.entity.Student;
import com.avgoi.schoolmgmt.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('student:view')")
    public ResponseEntity<List<Student>> list() {
        return ResponseEntity.ok(studentRepository.findAll());
    }
}
