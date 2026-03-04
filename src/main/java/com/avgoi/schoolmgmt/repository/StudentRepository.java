package com.avgoi.schoolmgmt.repository;

import com.avgoi.schoolmgmt.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByAcademicYear(Integer academicYear);
}
