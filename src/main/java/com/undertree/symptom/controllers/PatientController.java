package com.undertree.symptom.controllers;

import com.undertree.symptom.domain.Patient;
import com.undertree.symptom.exceptions.NotFoundException;
import com.undertree.symptom.repositories.PatientRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.beans.FeatureDescriptor;
import java.util.stream.Stream;

// https://spring.io/understanding/REST

@RestController
public class PatientController {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @PostMapping(Patient.RESOURCE_PATH)
    public Patient addPatient(@Valid @RequestBody Patient patient) {
        return patientRepository.save(patient);
    }

    @GetMapping(Patient.RESOURCE_PATH + "/{id}")
    public Patient getPatient(@PathVariable("id") Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Resource %s/%d not found", Patient.RESOURCE_PATH, id)));
    }

    @PutMapping(Patient.RESOURCE_PATH + "/{id}")
    public Patient updatePatientIncludingNulls(@PathVariable("id") Long id, @Valid @RequestBody Patient patient) {
        Patient aPatient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Resource %s/%d not found", Patient.RESOURCE_PATH, id)));
        // copy bean properties including nulls
        BeanUtils.copyProperties(patient, aPatient);
        return patientRepository.save(aPatient);
    }

    @PatchMapping(Patient.RESOURCE_PATH + "/{id}")
    public Patient updatePatientExcludingNulls(@PathVariable("id") Long id, @Valid @RequestBody Patient patient) {
        Patient aPatient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Resource %s/%d not found", Patient.RESOURCE_PATH, id)));
        // copy bean properties excluding nulls
        BeanUtils.copyProperties(patient, aPatient, getNullPropertyNames(patient));
        return patientRepository.save(aPatient);
    }

    @DeleteMapping(Patient.RESOURCE_PATH + "/{id}")
    public void deletePatient(@PathVariable("id") Long id) {
        if (patientRepository.findOne(id) != null) {
            patientRepository.delete(id);
        }
    }

    ///

    // TODO put in a more appropriate util class
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);

        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
}
