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

package io.undertree.symptom.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Predicate;
import io.undertree.symptom.domain.Patient;
import io.undertree.symptom.exceptions.ConflictException;
import io.undertree.symptom.exceptions.NotFoundException;
import io.undertree.symptom.repositories.PatientRepository;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import static io.undertree.symptom.repositories.PatientRepository.Predicates.hasAnyNameContaining;

// https://spring.io/understanding/REST
// http://www.restapitutorial.com/

/**
 * REST endpoint for the Patient resource.  Provides basic CRUD functionality
 * as well as various searching capabilities.
 *
 * @author Shawn Sherwood
 */
@RestController
@RequestMapping(Patient.RESOURCE_PATH)
public class PatientController {

	//private static final int DEFAULT_PAGE_SZ = 30;
	private static final ExampleMatcher DEFAULT_MATCHER = ExampleMatcher
			.matching()
			.withIgnorePaths("patientId")
			.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
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
	 * FYI - It is important that there are no setter methods for fields on the objTo that you don't
	 * want to be changed (e.g. ids).
	 * <p>
	 * TODO - move to EntityUtils?  Needs a little more design work...
	 *
	 * @param id the resource Id
	 * @param objTo the object to merge copy to
	 * @param objFrom the object to merge copy from
	 * @param <T> the resource type
	 * @param <ID> the resource id type
	 */
	static <T, ID extends Serializable> void updateProperties(ID id, T objTo, Object objFrom) {
		try {
			BeanUtils.copyProperties(objTo, objFrom);
		}
		catch (Exception ex) {
			throw new ConflictException(Patient.RESOURCE_PATH,
					String.format("Unable to update resource %s", id.toString()), ex);
		}
	}

	/**
	 * Creates a new instance of the entity type.  For this use with JPA, the
	 * backing datasource will provide the identity back to assist with further
	 * interactions.
	 *
	 * @param patient the Patient object to save
	 * @return An instance of the newly created Patient
	 */
	@PostMapping
	public Patient addPatient(@Valid @RequestBody final Patient patient) {
		return this.patientRepository.save(patient);
	}

	/**
	 * Returns a single instance of the specific entity.  If a request for an entity can't be located
	 * then a 404 error code should be returned to the client.
	 *
	 * @param patientId unique patient UUID to find
	 * @return A single patient with the matching patientId
	 */
	@GetMapping("/{id}")
	@Cacheable(value = "patients", key = "#patientId?.toString()")
	public Patient getPatient(@PathVariable("id") final UUID patientId) {
		return this.patientRepository.findByPatientId(patientId)
				.orElseThrow(() ->
						new NotFoundException(Patient.RESOURCE_PATH,
								String.format("Patient resource %s not found", patientId)));
	}

	/**
	 * Update an existing resource with a new representation.  The entire state of the entity is
	 * replaced with that provided with the RequestBody (this means that null or excluded fields are
	 * updated to null on the entity itself).
	 *
	 * @param patientId unique patient UUID to find
	 * @param patient the Patient to update with
	 * @return the Patient as modified by the update
	 */
	@PutMapping("/{id}")
	public Patient updatePatientIncludingNulls(@PathVariable("id") final UUID patientId,
			@Valid @RequestBody final Patient patient) {
		Patient originalPatient = this.getPatient(patientId);
		updateProperties(patientId, originalPatient, patient);
		return this.patientRepository.save(originalPatient);
	}

	/**
	 * Applies changes to an existing resource as described by the JSON Merge Patch RFC
	 * (https://tools.ietf.org/html/rfc7386).
	 * <p>
	 * Unlike PUT, the PATCH operation is intended apply delta changes as opposed to a complete
	 * resource replacement.  Like PUT this operation verifies that a resource exists by first
	 * loading it and then copies the properties from the RequestBody Map (i.e. any property that is
	 * in the map -- null values can be set using this technique).
	 * <p>
	 * TODO needs more testing to verify that it complies with the RFC
	 *
	 * @param patientId  the UUID of the Patient to patch
	 * @param patientMap a JSON map of properties to use as the merge patch source
	 * @return the Patient as modified by the merge patch
	 */
	@PatchMapping("/{id}")
	public Patient updatePatientExcludingNulls(@PathVariable("id") final UUID patientId,
			@RequestBody final Map<String, Object> patientMap) {
		Patient originalPatient = this.getPatient(patientId);
		updateProperties(patientId, originalPatient, patientMap);
		return this.patientRepository.save(originalPatient);
	}

	/**
	 * Used to delete a resource at {id}.  To keep the DELETE operation appear idempotent we will do a
	 * findOne before the actual delete so the client won't see an error if the delete is trying to
	 * remove something that doesn't exist.
	 * <p>
	 * Note: From a concurrency/transactional perspective it is still possible to get an error if
	 * multiple clients attempt to remove the same resource at the same time with this
	 * implementation.
	 *
	 * @param patientId the unique Id of the Patient to be removed
	 */
	@DeleteMapping("/{id}")
	public void deletePatient(@PathVariable("id") final UUID patientId) {
		this.patientRepository.findByPatientId(patientId)
				.ifPresent(this.patientRepository::delete);
	}

	/**
	 * Returns a "paged" collection of resources.  Pagination requests are captured as parameters on
	 * the request using "page=X" and "size=y" (ex. /patients?page=2&size=10).  The default is page 0
	 * and size 20 however, we have overridden the default to 30 using @PagableDefault as an example.
	 *
	 * @param pageable a Pageable to restrict results
	 * @return A paged result of matching Patients
	 */
	@GetMapping
	public Page<Patient> getPatients(final Pageable pageable) {
		Page<Patient> pagedResults = this.patientRepository.findAll(pageable);

		if (!pagedResults.hasContent()) {
			throw new NotFoundException(Patient.RESOURCE_PATH, "Patient resources not found");
		}

		return pagedResults;
	}

	/**
	 * Returns a "paged" collection of resources matching the input query params using default
	 * matching rules for strings of "contains and ignores case".
	 * <p>
	 * TODO I'm not exactly happy with the resource name "queryByExample".  Need to research more what
	 * other APIs look like for this kind of functionality
	 *
	 * @param paramMap a Map of fields to use to search with
	 * @param pageable a Pageable to restrict results
	 * @return A paged result of matching Patients
	 */
	@GetMapping("/search/by-example")
	public Page<Patient> getPatientsByExample(@RequestParam Map<String, Object> paramMap, Pageable pageable) {
		// naively copies map entries to matching properties in the Patient POJO
		Patient examplePatient = this.jacksonObjectMapper.convertValue(paramMap, Patient.class);

		Page<Patient> pagedResults = this.patientRepository
				.findAll(Example.of(examplePatient, DEFAULT_MATCHER), pageable);

		if (!pagedResults.hasContent()) {
			throw new NotFoundException(Patient.RESOURCE_PATH,
					"No Patients found matching example query " + examplePatient);
		}

		return pagedResults;
	}

	/**
	 * Another alternative to QBE via QueryDsl Predicates.
	 *
	 * @param predicate the Predicate to use to query with
	 * @param pageable a Pageable to restrict results
	 * @return A paged result of matching Patients
	 */
	@GetMapping("/search/predicate")
	public Page<Patient> getPatientsByPredicate(
			@QuerydslPredicate(root = Patient.class) final Predicate predicate, final Pageable pageable) {
		Page<Patient> pagedResults = this.patientRepository.findAll(predicate, pageable);

		if (!pagedResults.hasContent()) {
			throw new NotFoundException(Patient.RESOURCE_PATH,
					"No Patients found matching predicate " + predicate);
		}

		return pagedResults;
	}

	/**
	 * Search for Patients by name.
	 *
	 * @param name the name to search on
	 * @param pageable a Pageable to restrict results
	 * @return A paged result of matching Patients
	 */
	@GetMapping("/search/by-name")
	public Page<Patient> getPatientsHasAnyNameContaining(
			@RequestParam("name") final String name, final Pageable pageable) {
		Page<Patient> pagedResults = this.patientRepository
				.findAll(hasAnyNameContaining(name), pageable);

		if (!pagedResults.hasContent()) {
			throw new NotFoundException(Patient.RESOURCE_PATH,
					"No Patients found matching predicate " + hasAnyNameContaining(name));
		}

		return pagedResults;
	}
}
