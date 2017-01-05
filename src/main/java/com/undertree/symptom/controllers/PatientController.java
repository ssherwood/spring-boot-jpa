/*
 * Copyright 2016-2017 Shawn Sherwood
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.undertree.symptom.controllers;

import static com.undertree.symptom.repositories.PatientRepository.Predicates.hasAnyNameContaining;
import static org.springframework.data.domain.ExampleMatcher.StringMatcher.CONTAINING;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undertree.symptom.domain.Patient;
import com.undertree.symptom.exceptions.NotFoundException;
import com.undertree.symptom.repositories.PatientRepository;
import com.undertree.symptom.utils.BeanUtilsUtils;
import java.util.Map;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// https://spring.io/understanding/REST
// http://www.restapitutorial.com/

@RestController
public class PatientController {

  private static final ExampleMatcher DEFAULT_MATCHER = ExampleMatcher.matching()
      .withIgnorePaths("patientId")
      .withStringMatcher(CONTAINING)
      .withIgnoreCase();

  private final PatientRepository patientRepository;
  private final ObjectMapper jacksonObjectMapper;

  @Autowired
  public PatientController(final PatientRepository patientRepository,
      final ObjectMapper jacksonObjectMapper) {
    this.patientRepository = patientRepository;
    this.jacksonObjectMapper = jacksonObjectMapper;
  }

  /**
   * Creates a new instance of the entity type.  For this use with JPA, the backing datasource will
   * provide the identity back to assist with further interactions.
   *
   * @param patient the Patient object to save
   * @return
   */
  @PostMapping(Patient.RESOURCE_PATH)
  public Patient addPatient(@Valid @RequestBody final Patient patient) {
    return patientRepository.save(patient);
  }

  /**
   * Returns a single instance of the specific entity.  If a request for an entity can't be located
   * then a 404 error code should be returned to the client.
   *
   * @param patientId unique patient UUID to find
   * @return
   */
  @GetMapping(Patient.RESOURCE_PATH + "/{id}")
  public Patient getPatient(@PathVariable("id") final UUID patientId) {
    return patientRepository.findByPatientId(patientId)
        .orElseThrow(() ->
            new NotFoundException(
                String.format("Resource %s/%s not found", Patient.RESOURCE_PATH, patientId)));
  }

  /**
   * Update an existing resource with a new representation.  The entire state of the entity is
   * replaced with that provided with the RequestBody (this means that null or excluded fields are
   * updated to null on the entity itself).
   */
  @PutMapping(Patient.RESOURCE_PATH + "/{id}")
  public Patient updatePatientIncludingNulls(@PathVariable("id") final UUID patientId,
      @Valid @RequestBody final Patient patient) {
    Patient aPatient = patientRepository.findByPatientId(patientId)
        .orElseThrow(() ->
            new NotFoundException(
                String.format("Resource %s/%s not found", Patient.RESOURCE_PATH, patientId)));
    // copy bean properties including nulls
    BeanUtils.copyProperties(patient, aPatient);
    return patientRepository.save(aPatient);
  }

  /**
   * Applies changes to an existing resource.  Unlike PUT, the PATCH operation is intended apply
   * delta changes as opposed to an complete resource replacement.  Like PUT this operation verifies
   * that a resource exists by first loading it and them copies the non-null properties from the
   * RequestBody (i.e. any property that is set).
   */
  @PatchMapping(Patient.RESOURCE_PATH + "/{id}")
  public Patient updatePatientExcludingNulls(@PathVariable("id") final UUID patientId, /*@Valid*/
      @RequestBody final Patient patient) {
    Patient aPatient = patientRepository.findByPatientId(patientId)
        .orElseThrow(() ->
            new NotFoundException(
                String.format("Resource %s/%s not found", Patient.RESOURCE_PATH, patientId)));
    // copy bean properties excluding nulls
    BeanUtils.copyProperties(patient, aPatient, BeanUtilsUtils.getNullPropertyNames(patient));
    return patientRepository.save(aPatient);
  }

  /**
   * Used to delete a resource at {id}.  To keep the DELETE operation appear idempotent we will do a
   * findOne before the actual delete so the client won't see an error if the delete is trying to
   * remove something that doesn't exist.
   *
   * Note: From a concurrency/transactional perspective it is still possible to get an error if
   * multiple clients attempt to remove the same resource at the same time with this
   * implementation.
   */
  @DeleteMapping(Patient.RESOURCE_PATH + "/{id}")
  public void deletePatient(@PathVariable("id") final UUID patientId) {
    patientRepository.findByPatientId(patientId)
        .ifPresent(p -> patientRepository.delete(p.getId()));
  }

  /**
   * Returns a "paged" collection of resources.  Pagination requests are captured as parameters on
   * the request using "page=X" and "size=y" (ex. /patients?page=2&size=10).  The default is page 0
   * and size 20 however, we have overridden the default to 30 using @PagableDefault as an example.
   */
  @GetMapping(Patient.RESOURCE_PATH)
  public Page<Patient> getPatients(@PageableDefault(size = 30) final Pageable pageable) {
    Page<Patient> pagedResults = patientRepository.findAll(pageable);

    if (!pagedResults.hasContent()) {
      throw new NotFoundException(String.format("Resource %s not found", Patient.RESOURCE_PATH));
    }

    return pagedResults;
  }

  /**
   * Returns a "paged" collection of resources matching the input query params using default
   * matching rules for strings of "contains and ignores case".
   *
   * TODO I'm not exactly happy with the resource name "queryByExample".  Need to research more what
   * other APIs look like for this kind of functionality
   */
  @GetMapping(Patient.RESOURCE_PATH + "/queryByExample")
  public Page<Patient> getPatientsByExample(@RequestParam Map<String, Object> paramMap,
      @PageableDefault(size = 30) Pageable pageable) {
    // naively copies map entries to matching properties in the Patient POJO
    Patient examplePatient = jacksonObjectMapper.convertValue(paramMap, Patient.class);

    Page<Patient> pagedResults = patientRepository
        .findAll(Example.of(examplePatient, DEFAULT_MATCHER), pageable);

    if (!pagedResults.hasContent()) {
      throw new NotFoundException(String.format("Resource %s not found", Patient.RESOURCE_PATH + "/queryByExample"));
    }

    return pagedResults;
  }

  /**
   *
   * @param name
   * @param pageable
   * @return
   */
  @GetMapping(Patient.RESOURCE_PATH + "/search")
  public Page<Patient> getPatientsHasAnyNameContaining(@RequestParam("name") String name,
      @PageableDefault(size = 30) Pageable pageable) {
    Page<Patient> pagedResults = patientRepository.findAll(hasAnyNameContaining(name), pageable);

    if (!pagedResults.hasContent()) {
      throw new NotFoundException(String.format("Resource %s not found", Patient.RESOURCE_PATH + "/search"));
    }

    return pagedResults;
  }
}
