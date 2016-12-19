package com.undertree.symptom.domain;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

public class TestPatientBuilder {
    private final Patient testPatient = new Patient();

    public TestPatientBuilder() {
        // Start out with a valid randomized patient
        testPatient.setGivenName(RandomStringUtils.randomAlphabetic(2, 30));
        testPatient.setAdditionalName(RandomStringUtils.randomAlphabetic(2, 30));
        testPatient.setFamilyName(RandomStringUtils.randomAlphabetic(2, 30));
        LocalDate start = LocalDate.of(1949, Month.JANUARY, 1);
        long days = ChronoUnit.DAYS.between(start, LocalDate.now());
        testPatient.setBirthDate(start.plusDays(RandomUtils.nextLong(0, days + 1)));
        testPatient.setEmail(String.format("%s@%s.com", RandomStringUtils.randomAlphanumeric(20),
                RandomStringUtils.randomAlphanumeric(20)));
        testPatient.setGender(Gender.values()[RandomUtils.nextInt(0, Gender.values().length)]);
        testPatient.setHeight((short) RandomUtils.nextInt(140, 300));
        testPatient.setWeight((short) RandomUtils.nextInt(50, 90));
    }

    public TestPatientBuilder withGivenName(String givenName) {
        testPatient.setGivenName(givenName);
        return this;
    }

    public TestPatientBuilder withFamilyName(String familyName) {
        testPatient.setFamilyName(familyName);
        return this;
    }

    public TestPatientBuilder withAdditionalName(String additionalName) {
        testPatient.setAdditionalName(additionalName);
        return this;
    }

    public TestPatientBuilder withBirthDate(LocalDate birthDate) {
        testPatient.setBirthDate(birthDate);
        return this;
    }

    public TestPatientBuilder withEmail(String email) {
        testPatient.setEmail(email);
        return this;
    }

    public TestPatientBuilder withGender(Gender gender) {
        testPatient.setGender(gender);
        return this;
    }

    public TestPatientBuilder withHeight(Short height) {
        testPatient.setHeight(height);
        return this;
    }

    public TestPatientBuilder withWeight(Short weight) {
        testPatient.setWeight(weight);
        return this;
    }

    public Patient build() {
        return testPatient;
    }
}
