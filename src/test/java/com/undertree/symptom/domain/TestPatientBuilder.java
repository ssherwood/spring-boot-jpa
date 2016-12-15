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
        testPatient.setFamilyName(RandomStringUtils.randomAlphabetic(2, 30));
        LocalDate start = LocalDate.of(1949, Month.JANUARY, 1);
        long days = ChronoUnit.DAYS.between(start, LocalDate.now());
        testPatient.setBirthDate(start.plusDays(RandomUtils.nextLong(0, days + 1)));
    }

    public TestPatientBuilder withGivenName(String givenName) {
        testPatient.setGivenName(givenName);
        return this;
    }

    public TestPatientBuilder withFamilyName(String familyName) {
        testPatient.setFamilyName(familyName);
        return this;
    }

    public TestPatientBuilder withBirthDate(LocalDate birthDate) {
        testPatient.setBirthDate(birthDate);
        return this;
    }

    public Patient build() {
        return testPatient;
    }
}
