package com.undertree.symptom.controllers;

import com.undertree.symptom.domain.Patient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT)
public class TestPatientController {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void test_PatientController_getPatient_Expect_Patient1_Exists() {
        ResponseEntity<Patient> entity = restTemplate.getForEntity("/patient/1", Patient.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Patient aPatient = entity.getBody();
        assertThat(aPatient).isNotNull();
        assertThat(aPatient.getGivenName()).isEqualTo("Phillip");
        assertThat(aPatient.getFamilyName()).isEqualTo("Spec");
        assertThat(aPatient.getBirthDate()).isEqualTo(LocalDate.of(1972, 5, 5));
    }

    @Test
    public void test_PatientController_getPatient_Expect_Patient999_NotFound() {
        ResponseEntity<Patient> entity = restTemplate.getForEntity("/patient/999", Patient.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void test_PatientController_getPatient_Expect_Invalid() {
        String response = restTemplate.getForObject("/patient/foo", String.class);

        assertThat(response).contains("Bad Request");
    }
}
