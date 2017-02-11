---
layout: post
title:  "Create a JPA 'findBy' Implementation"
date: 2017-01-30 12:00:00 -0500
categories: spring rest
---

Since the default behavior of `findOne` really isn't all that desirable, lets add a more
appropriate method to the Repository interface:

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findById(Long id);
}
```

And change the GET implementation in the controller to:

```java
    @GetMapping("/patients/{id}")
    public Patient getPatient(@PathVariable("id") Long id) {
        return patientRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
    }
```

Apply these changes and restart.

Now if we `curl` again:

```
curl -sS localhost:8080/patients/1 | jq
```

We will at least get a 500 status error code.  However, that is not the most desirable response
from a REST API.

// TODO Discuss Spring's lack of standard http error exceptions and handlers
