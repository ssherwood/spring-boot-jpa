---
layout: post
title:  "Create a REST Controller"
date: 2017-01-23 12:00:00 -0500
categories: spring rest spring-mvc
---

Create a package called `controllers` in the application package and then create a
`PatientController` class within it.  Add the following code to the class:

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

When the application starts, you should be able to make "RESTful" GET calls on the `/patients`
resource.  Let's try one out at: [http://localhost:8080/patients/1](http://localhost:8080/patients/1).

However, _nothing is displayed_ in the browser, the HTTP response is just an empty page (and that
result seems a bit odd).  Intuitively you should already be thinking that there can't be a resource
at "patients/1" because we haven't created it yet (and you would be correct).  However, our
expectation should also be that when a resource is not found, we should receive a `404 NOT FOUND`
error as a response.

The answer to this little puzzle lays in the nuanced default behavior of the built-in `findOne`
implementation.  From the Spring [documentation](http://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/repository/CrudRepository.html#findOne-ID-),
we see that the Returns block for this method says:

> the entity with the given id or null if none found

Since the null result is happily passed back up the call stack, the response is processes as a
success with an empty payload.

## Create a JPA 'findBy' Implementation

Lets crete a more appropriate method to the Repository interface, one that we can exert more control
over to get the desired behavior.  In the PatientRepository, add a `findById` interface method:

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findById(Long id);
}
```

Here we are leveraging the Spring Data `findBy` query naming convention to produce a JPA query that
will fetch a Patient using the `id` field.  We're also taking advantage of a Java 8 feature by
wrapping the Patient response with an Optional<>.  This means that even null results returned by
the repository will be wrapped in a usable Object.

Finally, change the @GetMapping implementation in the Patient Controller to use the new query:

```java
@GetMapping("/patients/{id}")
public Patient getPatient(@PathVariable("id") Long id) {
    return patientRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
}
```

The `orElseThrows()` is a method call chained onto the Optional return type.  This construct is null
safe and frees us from having to write (ugly?) null checking code.  If there is a null response,
this method will throw a built-in Java exception with a message indicating that the requested
resource was not found.

**Restart the application.**

Use `curl` this time on the `/patients/1` resource:

```bash
curl -sS localhost:8080/patients/1 | jq
```

The result is a 500 status error code. [Sad Trombone].
  
Wait, I thought this approach was supposed to help?  A 500 error code is even worse than the empty
200 because it implies that there is something wrong with the server.  This could have real-world
implications if the application were being hosted behind a reverse proxy configured to remove
instances returning "unhealthy" response codes.

To be fair, this is partially my own fault.

For expediency, I chose to use an existing Java runtime exception and the Spring MVC response
handler treats it as an unhandled exception and returns a 500 response.  There are multiple ways to
address this behavior but for now, we will create a new type of exception that Spring will handle
correctly.

// TODO Discuss Spring's lack of standard HTTP error exceptions and why 500 exceptions are a poor
default behavior.

## Create a Custom Exception

To fix this, create an `exceptions` package in the application root and then add a Java class called
`NotFoundException` with the following code:

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super();
    }
    
    public NotFoundException(String message) {
        super(message);
    }
}
```

Modify the Patient GET method to throw this new exception instead of the original
`IllegalArgumentException`:

```java
        return patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Patient %d not found", id)));
```

Restart the application and attempt the `curl` command again.

```json
{
  "timestamp": 1481488691203,
  "status": 404,
  "error": "Not Found",
  "exception": "io.undertree.symptom.exceptions.NotFoundException",
  "message": "Patient 1 not found",
  "path": "/patients/1"
}
```

That looks much better!  By marking the Exception with a `@ResponseStatus`, the default exception
handler interprets the exception as a 404 and returns the appropriate error block.  This is exactly
what we and our eventual clients should expect.
