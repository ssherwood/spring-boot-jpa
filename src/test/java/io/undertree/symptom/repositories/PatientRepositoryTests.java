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
package io.undertree.symptom.repositories;

import io.undertree.symptom.domain.GivenName;
import io.undertree.symptom.domain.Patient;
import io.undertree.symptom.domain.TestPatientBuilder;
import io.undertree.symptom.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// refer to:
// https://joel-costigliola.github.io/assertj/
// http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html
// https://spring.io/blog/2016/04/15/testing-improvements-in-spring-boot-1-4
// http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#__sql

@DataJpaTest
public class PatientRepositoryTests {

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void test_PatientRepository_findByPatientId_ExpectExists() throws Exception {
        Patient patient = entityManager.persistFlushFind(new TestPatientBuilder().build());
        Patient aPatient = patientRepository.findByPatientId(patient.getPatientId()).orElseThrow(NotFoundException::new);
        assertThat(aPatient).isEqualTo(patient);
    }

    @Test
    public void test_PatientRepository_SaveWithNull_ExpectException() throws Exception {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            patientRepository.saveAndFlush(null);
        });
    }

    @Test
    public void test_PatientRepository_SaveWithEmpty_ExpectException() throws Exception {
        ConstraintViolationException exception =
				assertThrows(ConstraintViolationException.class,
						() -> patientRepository.saveAndFlush(new Patient()));

        assertThat(exception).hasMessageContaining("'must not be blank'");
    }

    @Test
    public void test_PatientRepository_SaveWithEmptyGivenName_ExpectException() throws Exception {
        ConstraintViolationException exception =
				assertThrows(ConstraintViolationException.class,
						() -> patientRepository.saveAndFlush(new TestPatientBuilder().withGivenName(new GivenName("")).build()));

        assertThat(exception)
                .hasMessageContaining("givenName.givenName")
                .hasMessageContaining("'The given name should only contain alphanumeric values.'")
                .hasMessageContaining("'size must be between 2 and 50'");
    }

    @Test
    public void test_PatientRepository_SaveWithEmptyFamilyName_ExpectException() throws Exception {

        ConstraintViolationException exception =
				assertThrows(ConstraintViolationException.class,
						() -> patientRepository.saveAndFlush(new TestPatientBuilder().withFamilyName("").build()));

        assertThat(exception)
                .hasMessageContaining("familyName")
                .hasMessageContaining("'must not be blank'");
    }

    @Test
    public void test_PatientRepository_SaveWithShortGivenName_ExpectException() throws Exception {

    	ConstraintViolationException exception =
				assertThrows(ConstraintViolationException.class,
						() -> patientRepository.saveAndFlush(new TestPatientBuilder().withGivenName(new GivenName("A")).build()));

        assertThat(exception)
                .hasMessageContaining("givenName")
                .hasMessageContaining("'size must be between 2 and");
    }

    @Test
    public void test_PatientRepository_SaveWithShortFamilyName_ExpectException() throws Exception {

        ConstraintViolationException exception =
				assertThrows(ConstraintViolationException.class,
						() -> patientRepository.saveAndFlush(new TestPatientBuilder().withFamilyName("Z").build()));

        assertThat(exception)
                .hasMessageContaining("familyName")
                .hasMessageContaining("'size must be between 2 and");
    }


    @Test
    public void test_PatientRepository_SaveWithInvalidEmail_ExpectException() throws Exception {

        ConstraintViolationException exception =
				assertThrows(ConstraintViolationException.class,
						() -> patientRepository.saveAndFlush(new TestPatientBuilder().withEmail("baz").build()));

        assertThat(exception)
                .hasMessageContaining("email")
                .hasMessageContaining("'must be a well-formed email address'");
    }


    @Test
    public void test_PatientRepository_SaveWithLessThanMinHeight_ExpectException() throws Exception {

        ConstraintViolationException exception =
				assertThrows(ConstraintViolationException.class,
						() -> patientRepository.saveAndFlush(new TestPatientBuilder().withHeight((short) -1).build()));

        assertThat(exception)
                .hasMessageContaining("height")
                .hasMessageContaining("'must be greater than or equal to 0'");
    }


    @Test
    public void test_PatientRepository_SaveWithLessThanMinWeight_ExpectException() throws Exception {

        ConstraintViolationException exception =
				assertThrows(ConstraintViolationException.class,
						() -> patientRepository.saveAndFlush(new TestPatientBuilder().withWeight((short) -1).build()));

        assertThat(exception)
                .hasMessageContaining("weight")
                .hasMessageContaining("'must be greater than or equal to 0'");
    }

    @Test
    public void test_PatientRepository_SaveWithFutureBirthDayInFuture_ExpectException() throws Exception {
        ConstraintViolationException exception =
				assertThrows(ConstraintViolationException.class,
						() -> patientRepository.saveAndFlush(new TestPatientBuilder().withBirthDate(LocalDate.now().plusDays(1)).build()));

        assertThat(exception)
                .hasMessageContaining("birthDate")
                .hasMessageContaining("'must be a past date'");
    }
}