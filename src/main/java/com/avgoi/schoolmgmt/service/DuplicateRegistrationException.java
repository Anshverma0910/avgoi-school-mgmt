package com.avgoi.schoolmgmt.service;

/**
 * Thrown when attempting to register a school with an existing registration ID.
 */
public class DuplicateRegistrationException extends RuntimeException {

    public DuplicateRegistrationException(String message) {
        super(message);
    }
}
