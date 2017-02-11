---
layout: post
title:  "Set up a REST Controller"
date: 2017-01-23 12:00:00 -0500
categories: spring rest
---

Create a package called `controllers` in the default package and create a `PatientController` class
within it.  Then add the following code to the class: 

```java
@RestController
public class PatientController {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }
    
    @GetMapping("/patients/{id}")
    public Patient getPatient(@PathVariable("id") Long id) {
        return patientRepository.findOne(id);
    }
}
```

Restart the application.

After the application restarted you should now be able to make "RESTful" calls against the
`/patients` at [http://localhost:8080/patients/1](http://localhost:8080/patients/1).

However, you will notice that nothing is displayed... and that is weird.

// TODO discuss findOne default behavior of returning null