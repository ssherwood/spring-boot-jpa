---
layout: post
title:  "Add a Patient via POST Method"
date: 2017-01-30 12:00:00 -0500
categories: spring rest
---

We have a functional GET but now we need a way to add a Patient.  Using REST semantics this should
be expressed with a POST.  To support this, add the following code to the PatientController:

```java
    @PostMapping("/patients")
    public Patient addPatient(@RequestBody Patient patient) {
        return patientRepository.save(patient);
    }
```

Restart and issue a `curl -H "Content-Type: application/json" -X POST localhost:8080/patient -d '{}'`

Interestingly we get a "{}" back.  If we look at the logs we should be able to see that an insert
did take place but where is the id of the entity we just created?

If we open up the H2 console again, we should see that a row was created with the ID of 1.  If we
use our GET operation, we should get the entity back right?

```
curl -sS localhost:8080/patients/1 | jq
```

Still the same empty empty JSON object?

The reason we don't see the id field is that we failed to provide a getter method on the entity
class.  The default Jackson library that Spring uses to convert the object to JSON needs getters
and setters to be able to access object's internal values.

Modify the Patient class to add a getter for the id field:

```java
    public Long getId() {
        return id;
    }
```

Now when you execute the POST we can see an id field on the JSON object

```bash
curl -sS -H "Content-Type: application/json" -X POST localhost:8080/patients -d '{}' | jq
```

// TODO discuss Lombok