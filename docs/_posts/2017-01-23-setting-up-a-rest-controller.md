---
layout: post
title:  "Set up a REST Controller"
date: 2017-01-23 12:00:00 -0500
categories: spring rest spring-mvc
---

Create a package called `controllers` in the application base and create a `PatientController`
class within it.  Then add the following code:

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

**Restart the application.**

When the application start back up, you should be able to make "RESTful" GET calls on the
`/patients` resource.  Let's try one on: [http://localhost:8080/patients/1](http://localhost:8080/patients/1).

However, nothing is displayed in the browser, the response is just an empty page (and that result
seems a bit odd).  Intuitively you should already be thinking that there can't be a "patients/1"
because we haven't created it yet (and you would be correct).  However, our expectation should be
that when a resource is not found that we should receive a `404 NOT FOUND` error.

The answer to this little puzzle lays in the nuanced behavior of the default `findOne`
implementation.  From the Spring [documentation](http://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/repository/CrudRepository.html#findOne-ID-),
we see that the Returns block says:

> the entity with the given id or null if none found

Since the null result is happily passed back up the call stack, the response is processes as a
success with an empty payload.

## Create a JPA 'findBy' Implementation

Since the default behavior of the `findOne` method isn't exactly what we want in this scenario, lets
add a more appropriate method to the Repository interface:

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findById(Long id);
}
```

Here we are leveraging the Spring Data `findBy` query naming convention to produce a query that
fetches the Patient using the field named `id`.  We're also taking advantage of a new Java 8 feature
by wrapping the Patient response in an Optional<>.  This means that even null results will be
wrapped in a usable Object.

Finally, change the @GetMapping implementation in the Controller to use this query:

```java
@GetMapping("/patients/{id}")
public Patient getPatient(@PathVariable("id") Long id) {
    return patientRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
}
```

The `orElseThrows()` is a method call on the Optional return type.  This produces a nice call chain
that is null safe and frees us from having to write ugly null checking code.  If there is a null
response, we throw a built-in Java exception indicating that the requested resource was not found.

**Restart the application.**

If we use `curl` this time on the `/patients` resource:

```bash
curl -sS localhost:8080/patients/1 | jq
```

We get a 500 status error code. [sad trombone]
  
Wait, I thought this was supposed to help?  A 500 error code is even more undesirable than the empty
200 because it implies that there is something wrong with the server.  This could have real-world
implications if the application were being hosted behind a reverse proxy configured to remove
"unhealthy" instances.

Well, this is partially my own fault.  For expediency, I decided to reuse an existing Java runtime
exception and the Spring response handler didn't know how to deal with it.  As there are no built-in
HTTP error code exceptions in Java or Spring, we are just going to have to make some of our own.

// TODO Discuss Java and Spring's lack of standard http error exceptions and handlers
