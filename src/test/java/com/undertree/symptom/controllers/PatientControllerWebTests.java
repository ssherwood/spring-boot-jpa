package com.undertree.symptom.controllers;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.undertree.symptom.domain.Patient;
import com.undertree.symptom.domain.TestPatientBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
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
public class PatientControllerWebTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void test_PatientController_getPatient_Expect_Patient1_Exists() throws Exception {
        ResponseEntity<Patient> entity = restTemplate.getForEntity("/patient/1", Patient.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).isNotNull()
                .hasFieldOrPropertyWithValue("givenName", "Phillip")
                .hasFieldOrPropertyWithValue("familyName", "Spec")
                .hasFieldOrPropertyWithValue("birthDate", LocalDate.of(1972, 5, 5));
    }

    @Test
    public void test_PatientController_getPatient_Expect_NotFound() throws Exception {
        ResponseEntity<Patient> entity = restTemplate.getForEntity("/patient/99999999", Patient.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void test_PatientController_getPatient_Expect_BadRequest() throws Exception {
        String response = restTemplate.getForObject("/patient/foo", String.class);
        assertThat(response).contains("Bad Request");
    }

    @Test
    public void test_PatientController_addPatient_Expect_OK() throws Exception {
        ResponseEntity<Patient> entity = restTemplate.postForEntity("/patient",
                new TestPatientBuilder().build(), Patient.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).isNotNull()
                .hasNoNullFieldsOrProperties();
    }

    @Test
    public void test_PatientController_addPatient_WithEmpty_Expect_BadRequest() throws Exception {
        ResponseEntity<String> json = restTemplate.postForEntity("/patient", new Patient(), String.class);

        assertThat(json.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JSONAssert.assertEquals("{exception:\"org.springframework.web.bind.MethodArgumentNotValidException\"}", json.getBody(), false);
    }

    @Test
    public void test_PatientController_addPatient_WithEmptyGivenName_Expect_BadRequest() throws Exception {
        ResponseEntity<String> json = restTemplate.postForEntity("/patient",
                new TestPatientBuilder().withGivenName("").build(), String.class);

        assertThat(json.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JSONAssert.assertEquals("{exception:\"org.springframework.web.bind.MethodArgumentNotValidException\"}", json.getBody(), false);
    }

    @Test
    public void test_PatientController_addPatient_WithEmptyFamilyName_Expect_BadRequest() throws Exception {
        ResponseEntity<String> json = restTemplate.postForEntity("/patient",
                new TestPatientBuilder().withFamilyName("").build(), String.class);

        assertThat(json.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JSONAssert.assertEquals("{exception:\"org.springframework.web.bind.MethodArgumentNotValidException\"}", json.getBody(), false);
    }
}
