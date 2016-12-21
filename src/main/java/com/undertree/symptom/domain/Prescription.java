package com.undertree.symptom.domain;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Prescription {
    @Id
    @GeneratedValue
    private Long id;
    private String description;
    private LocalDate initialDate;
    private LocalDate expirationDate;
    // TODO Doctor doctor
    @OneToOne
    private Patient patient;
    @OneToOne
    private Medication medication;

    // schedule?
    // route?

    ///

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }

    public LocalDate getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(LocalDate initialDate) {
        this.initialDate = initialDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}
