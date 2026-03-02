package com.avgoi.schoolmgmt.repository;

import com.avgoi.schoolmgmt.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {

    Optional<School> findByRegistrationId(String registrationId);

    List<School> findByCity(String city);

    boolean existsByRegistrationId(String registrationId);
}
