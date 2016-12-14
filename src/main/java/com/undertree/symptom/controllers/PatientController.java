package com.undertree.symptom.controllers;

import com.undertree.symptom.domain.Patient;
import com.undertree.symptom.exceptions.NotFoundException;
import com.undertree.symptom.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

// https://spring.io/understanding/REST

@RestController
public class PatientController {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping("/patient/{id}")
    public Patient getPatient(@PathVariable("id") Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Patient %d not found", id)));
    }

    @PostMapping("/patient")
    public Patient addPatient(@Valid @RequestBody Patient patient) {
        return patientRepository.save(patient);
    }
}
