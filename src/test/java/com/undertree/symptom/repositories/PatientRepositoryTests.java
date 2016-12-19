package com.undertree.symptom.repositories;

import com.undertree.symptom.domain.Patient;
import com.undertree.symptom.domain.TestPatientBuilder;
import com.undertree.symptom.exceptions.NotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

// refer to:
// https://joel-costigliola.github.io/assertj/
// http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html
// https://spring.io/blog/2016/04/15/testing-improvements-in-spring-boot-1-4
// http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#__sql

@RunWith(SpringRunner.class)
@DataJpaTest
public class PatientRepositoryTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    PatientRepository patientRepository;

    @Test
    public void test_PatientRepository_FindById_ExpectExists() throws Exception {
        Long patientId = entityManager.persistAndGetId(new TestPatientBuilder().build(), Long.class);
        Patient aPatient = patientRepository.findById(patientId).orElseThrow(NotFoundException::new);
        assertThat(aPatient.getId()).isEqualTo(patientId);
    }

    @Test
    public void test_PatientRepository_SaveWithNull_ExpectException() throws Exception {
        thrown.expect(InvalidDataAccessApiUsageException.class);
        patientRepository.save((Patient) null);
    }

    @Test
    public void test_PatientRepository_SaveWithEmpty_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(containsString("'may not be empty'"));
        patientRepository.save(new Patient());
    }

    @Test
    public void test_PatientRepository_SaveWithEmptyGivenName_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(allOf(containsString("givenName"), containsString("'may not be empty'")));
        patientRepository.save(new TestPatientBuilder().withGivenName("").build());
    }

    @Test
    public void test_PatientRepository_SaveWithEmptyFamilyName_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(allOf(containsString("familyName"), containsString("'may not be empty'")));
        patientRepository.save(new TestPatientBuilder().withFamilyName("").build());
    }

    @Test
    public void test_PatientRepository_SaveWithShortGivenName_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(allOf(containsString("givenName"), containsString("'size must be between 2 and")));
        patientRepository.save(new TestPatientBuilder().withGivenName("A").build());
    }

    @Test
    public void test_PatientRepository_SaveWithShortFamilyName_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(allOf(containsString("familyName"), containsString("'size must be between 2 and")));
        patientRepository.save(new TestPatientBuilder().withFamilyName("Z").build());
    }

    @Test
    public void test_PatientRepository_SaveWithShortAdditionalName_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(allOf(containsString("additionalName"), containsString("'size must be between 2 and")));
        patientRepository.save(new TestPatientBuilder().withAdditionalName("Z").build());
    }

    @Test
    public void test_PatientRepository_SaveWithInvalidEmail_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(allOf(containsString("email"), containsString("'not a well-formed email address'")));
        patientRepository.save(new TestPatientBuilder().withEmail("baz").build());
    }

    @Test
    public void test_PatientRepository_SaveWithLessThanMinHeight_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(allOf(containsString("height"), containsString("'must be greater than or equal to 0'")));
        patientRepository.save(new TestPatientBuilder().withHeight((short) -1).build());
    }

    @Test
    public void test_PatientRepository_SaveWithLessThanMinWeight_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(allOf(containsString("weight"), containsString("'must be greater than or equal to 0'")));
        patientRepository.save(new TestPatientBuilder().withWeight((short) -1).build());
    }
}