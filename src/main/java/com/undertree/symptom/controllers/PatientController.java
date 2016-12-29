package com.undertree.symptom.controllers;

import com.undertree.symptom.domain.Patient;
import com.undertree.symptom.exceptions.NotFoundException;
import com.undertree.symptom.repositories.PatientRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.beans.FeatureDescriptor;
import java.util.List;
import java.util.stream.Stream;

// https://spring.io/understanding/REST
// http://www.restapitutorial.com/

@RestController
public class PatientController {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Creates a new instance of the entity type.  For this use with JPA, the backing datasource will provide the
     * identity back to assist with further interactions.
     *
     * @param patient
     * @return
     */
    @PostMapping(Patient.RESOURCE_PATH)
    public Patient addPatient(@Valid @RequestBody Patient patient) {
        return patientRepository.save(patient);
    }

    /**
     * Returns a single instance of the specific entity.  If a request for an entity can't be located then a 404
     * error code should be returned to the client.
     *
     * @param id
     * @return
     */
    @GetMapping(Patient.RESOURCE_PATH + "/{id}")
    public Patient getPatient(@PathVariable("id") Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Resource %s/%d not found", Patient.RESOURCE_PATH, id)));
    }

    /**
     * Update an existing resource with a new representation.  The entire state of the entity is replaced with
     * that provided with the RequestBody (this means that null or excluded fields are updated to null on the entity
     * itself).
     *
     * @param id
     * @param patient
     * @return
     */
    @PutMapping(Patient.RESOURCE_PATH + "/{id}")
    public Patient updatePatientIncludingNulls(@PathVariable("id") Long id, @Valid @RequestBody Patient patient) {
        Patient aPatient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Resource %s/%d not found", Patient.RESOURCE_PATH, id)));
        // copy bean properties including nulls
        BeanUtils.copyProperties(patient, aPatient);
        return patientRepository.save(aPatient);
    }

    /**
     * Applies changes to an existing resource.  Unlike PUT, the PATCH operation is intended apply delta changes
     * as opposed to an complete resource replacement.  Like PUT this operation verifies that a resource exists
     * by first loading it and them copies the non-null properties from the RequestBody (i.e. any property that
     * is set).
     *
     * TODO: I wonder if using the @Valid could cause issues later?  For example with "required" fields that don't
     * need to be included as part of a delta.  Yes, I think this will cause problems... but I'm not sure exactly
     * how to resolve it - validate only non-null fields?  How does one do that with Bean Validation?
     *
     * @param id
     * @param patient
     * @return
     */
    @PatchMapping(Patient.RESOURCE_PATH + "/{id}")
    public Patient updatePatientExcludingNulls(@PathVariable("id") Long id, @Valid @RequestBody Patient patient) {
        Patient aPatient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Resource %s/%d not found", Patient.RESOURCE_PATH, id)));
        // copy bean properties excluding nulls
        BeanUtils.copyProperties(patient, aPatient, getNullPropertyNames(patient));
        return patientRepository.save(aPatient);
    }

    /**
     * Used to delete a resource at {id}.  To keep the DELETE operation appear idempotent we will do a findOne
     * before the actual delete so the client won't see an error if the delete is trying to remove something that
     * doesn't exist.
     *
     * Note: From a concurrency/transactional perspective it is still possible to get an error if multiple clients
     * attempt to remove the same resource at the same time with this implementation.
     *
     * @param id
     */
    @DeleteMapping(Patient.RESOURCE_PATH + "/{id}")
    public void deletePatient(@PathVariable("id") Long id) {
        if (patientRepository.findOne(id) != null) {
            patientRepository.delete(id);
        }
    }

    /**
     * Returns a "paged" collection of resources.  Pagination requests are captured as parameters on the request
     * using "page=X" and "size=y" (ex. /patients?page=2&size=10).  The default is page 0 and size 20 however, we have
     * overridden the default to 30 using @PagableDefault as an example.
     *
     * @param pageable
     * @param response
     * @return
     */
    @GetMapping(Patient.RESOURCE_PATH)
    public List<Patient> getPatients(@PageableDefault(size = 30) Pageable pageable, HttpServletResponse response) {
        Page<Patient> pagedResults = patientRepository.findAll(pageable);

        setMetadataResponseHeaders(response, pageable, pagedResults);

        if (!pagedResults.hasContent()) {
            throw new NotFoundException(String.format("Resource %s not found", Patient.RESOURCE_PATH));
        }

        return pagedResults.getContent();
    }

    ///

    private void setMetadataResponseHeaders(HttpServletResponse response, Pageable pageable, Page pagedResults) {
        // TODO look into additional meta fields like first and last
        response.setHeader("X-Meta-Pagination",
                String.format("page-number=%d,page-size=%d,total-elements=%d,total-pages=%d",
                        pageable.getPageNumber(), pageable.getPageSize(),
                        pagedResults.getTotalElements(), pagedResults.getTotalPages()));
    }

    // TODO put in a more appropriate "Util" class or common base class
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);

        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
}
