package com.undertree.symptom.repositories;

import com.undertree.symptom.domain.Patient;
import com.undertree.symptom.exceptions.NotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

// refer to:
// https://joel-costigliola.github.io/assertj/
// http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html
// https://spring.io/blog/2016/04/15/testing-improvements-in-spring-boot-1-4
// http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#__sql

@RunWith(SpringRunner.class)
@DataJpaTest
public class PatientRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    PatientRepository patientRepository;

    @Test
    public void test_PatientRepository_FindById_ExpectExists() throws Exception {
        Long patientId = entityManager.persistAndGetId(new Patient(), Long.class);
        Patient aPatient = patientRepository.findById(patientId).orElseThrow(NotFoundException::new);
        assertThat(aPatient.getId()).isEqualTo(patientId);
    }

    // TODO make this more random
    private Patient createRandomPatient() {
        Patient randomPatient = new Patient();
        randomPatient.setBirthDate(randomDate(1970));
        randomPatient.setGivenName("random");
        randomPatient.setFamilyName("random");
        return randomPatient;
    }

    private LocalDate randomDate(int year) {
        LocalDate start = LocalDate.of(year, Month.JANUARY, 1);
        long days = ChronoUnit.DAYS.between(start, LocalDate.now());
        return start.plusDays(new Random().nextInt((int) days + 1));
    }
}
