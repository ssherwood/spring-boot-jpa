/*
 * Copyright 2017 Shawn Sherwood
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

import com.undertree.symptom.domain.Medication;
import com.undertree.symptom.repositories.MedicationRepository;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint for the Medication resource.
 *
 * Provides basic CRUD functionality as well as various searching capabilities.
 *
 * @author Shawn Sherwood
 */
@RestController
@RequestMapping(Medication.RESOURCE_PATH)
public class MedicationController {

  private final MedicationRepository medicationRepository;

  @Autowired
  public MedicationController(final MedicationRepository medicationRepository) {
    this.medicationRepository = medicationRepository;
  }

  @PostMapping
  public Medication addMedication(@Valid @RequestBody final Medication medication) {
    return medicationRepository.save(medication);
  }
}
