# Overview

This project is an attempt to revisit an older one that I did a few years
back when I took part in the Coursera Specialization on Android Development.
During the final Capstone we were given the choice of several projects to
implement and I chose one that was not only interesting but that I had a
strong personal connection with: an app for cancer patients to self report
on their pain symptoms.  A brief article was written about the Capstone 
on the Vanderbilt School of Engineering [web site](http://engineering.vanderbilt.edu/news/2014/capstone-app-project-for-mooc-aims-to-track-help-manage-cancer-patients-pain/).

## History

The original server-side implementation was my first time using Spring
Boot and since then, I've felt that there were many ways to improve upon
the original.  Since the final Capstone project was time limited I did
not get as much time to dedicate to the server side since I also had to
implement the whole Android front-end (something I had never done).

## Goals

I'd like to re-design the original server-side implementation and take
the opportunity to document the process so I can use it as a reference
application to share with other developers.



# Step 1

As with many Spring Boot apps, start with the [Spring Initializr](http://start.spring.io/).
In the Dependencies section, type in: `Web, Actuator, JPA, H2, and Devtools`
and select Generate.

Unzip the download and import the project into your IDE.

Run the application.  Depending on your IDE, this might be a right-click
"Run" command on the Application class that was automatically created for
you by the Initializr.

You should see several INFO commands printed out and Tomcat being started
on port 8080.  Try it out: http://localhost:8080

You'll see the "Whitelablel" error page.  This error page is Spring
Boot's way of telling you that you have encountered some kind of error.
In this case, it is just a basic 404 because we haven't implemented any
resource to respond to the GET.

Side Note: If you use `curl` instead of the browser, you'll get a JSON
response instead as Boot is attempting to detect the origin of the caller
and return the most appropriate response type.

Ultimately, we'll create a custom error handler for these scenarios but
for now lets jump into some actual coding.


# Step 2

Lets start by creating a domain object.  In JPA there are called Entities
and are signified with the @Entity annotation.

Create a `domain` package and create a Java class called `Patient` and
then add the following code:

```
@Entity
public class Patient {
    @Id
    @GeneratedValue
    private Long id;
}
```

Restart the application.

If you watch the logs closely, you'll see some additional information
being printed out from Hibernate (this is Spring Boot's default JPA
implementation).

What you may not have noticed however is that by including H2 you are
also running an in-memory database with a web console: http://localhost:8080/h2-console

FYI: Make sure you set the JDBC URL to 'jdbc:h2:mem:testdb' instead of 
the default or else you won't see the PATIENT table that was create when we
started the app (the Spring Boot default database URL).

This is nice but how do we get the application to be able to create,
read, update and delete Patients?


# Step 3

Create a package called `repositories` and create a `PatientRepository` 
interface.  Add the following code:

```
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
```

That's not a lot of code but what does it do?

TODO: talk about Spring Data JPA

TODO: talk about why I'm not using @RestRepositories

Before we get too far ahead of ourselves, lets enable some configurations
that will help with debugging.  Edit the `application.properties` file in
the src/main/resources folder and add:

```
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

I STRONGLY recommend adding these and keeping it set for all of your local
development lifecycle.  You'll always want to review the SQL that is being
created and executed on your behalf.

# Step 4

Create a package called `controllers` and create a `PatientController`
class in it.  Then add the following code: 

```java
@RestController
public class PatientController {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }
    
    @GetMapping("/patient/{id}")
    public Patient getPatient(@PathVariable("id") Long id) {
        return patientRepository.findOne(id);
    }
}
```

Restart the application.

After the restart you can now attempt RESTful calls against the /patient
URL like this: http://localhost:8080/patient/1

However, nothing is displayed... that's weird.

TODO discuss findOne behavior of returning null


# Step 5

Since the default behavior really isn't desirable, lets add an appropriate
method to the Repository interface:

```
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findById(Long id);
}
```

and change the GET implementation to:

```
    @GetMapping("/patient/{id}")
    public Patient getPatient(@PathVariable("id") Long id) {
        return patientRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
    }
```

Apply the changes and restart.

Now if we `curl -sS localhost:8080/patient/1 | jq` we at least get an error
but the 500 status code is not desirable.

TODO Discuss Spring's lack of standard http error exceptions and handlers

# Step 6

Create a package called `exceptions` and add a Java class called `NotFoundException`
with the following code:

```
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
```

and modify the GET implementation to throw this instead of the IllegalArgumentException:

```
        return patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Patient %d not found", id)));
```

Restart the application and attempt the `curl` command again.

```
{
  "timestamp": 1481488691203,
  "status": 404,
  "error": "Not Found",
  "exception": "com.undertree.symptom.exceptions.NotFoundException",
  "message": "Patient 1 not found",
  "path": "/patient/1"
}
```

That looks better!

# Step 7:  Add a Patient

We have a functional GET but now we need a way to add a Patient.  Using
REST semantics this should be expressed with a POST.  To support this,
add the following code to the PatientController:

```
    @PostMapping("/patient")
    public Patient addPatient(@RequestBody Patient patient) {
        return patientRepository.save(patient);
    }
```

Restart and issue a `curl -H "Content-Type: application/json" -X POST localhost:8080/patient -d '{}'`

Interestingly we get a "{}" back.  If we look at the logs we should be
able to see that an insert did take place but where is the id of the
entity we just created?

If we open up the H2 console again, we should see that a row was created
with the ID of 1.  If we use our GET operation, we should get the entity
back right?

```
curl -sS localhost:8080/patient/1 | jq
```

Still the same empty empty object?

The reason we don't see the id field is that we failed to provide a getter
method on the entity class.  The default Jackson library that Spring uses
to convert the object to JSON needs getters and setters to be able to
access object's internal values.

Modify the Patient class to add a getter for the id field:

```
    public Long getId() {
        return id;
    }
```

Now when you execute the POST we can see an id field on the JSON object

```
curl -sS -H "Content-Type: application/json" -X POST localhost:8080/patient -d '{}' | jq
```

TODO discuss Lombok

# Step 8:  Lets make this Patient more interesting

Add the following attributes to the Patient entity:

```
    private String givenName;
    private String familyName;
    private LocalDate birthDate;
```

Add the associated getter/setters using your IDEs code generator if
possible.

Using curl again submit a Patient add request:

```
curl -sS -H "Content-Type: application/json" -X POST localhost:8080/patient -d '{"givenName":"Max","familyName":"Colorado","birthDate":"1942-12-11"}' | jq
```

400 Error?  So that LocalDate is causes a JsonMappingException.

We need to add a Jackson dependency to the project so it knows how to
handle JSR 310 Dates (introduced in Java 8).  Add this to the pom.xml:

```
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.8.5</version>
</dependency>

```

Refresh the dependencies and restart the application.  Now we see an odd
looking date structure:

```
"birthDate": [
    1942,
    12,
    11
  ]
```

That isn't exactly what we want.  There is yet another configuration
change we need here to tell Jackson to format the date "correctly".

Update the application.properties and include:

```
spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS = false
```

Volia!

But wait.  If we look at how the date is actually stored in the database
through the H2 console, it looks like a BLOB.  That isn't good since
we might want to be able to query against it.

Why doesn't JPA support LocalDate?

# Step 9: Create a JPA Converter

As of the time of writing, JPA still does not natively support the JSR
310 dates.  It does, however, provide support for custom converters that
can.

Add a package called `converters` and add a class called `LocalDateAttributeConverter`.
 Then add the following code:
 
```
@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    public Date convertToDatabaseColumn(LocalDate locDate) {
        return (locDate == null ? null : Date.valueOf(locDate));
    }

    @Override
    public LocalDate convertToEntityAttribute(Date sqlDate) {
        return (sqlDate == null ? null : sqlDate.toLocalDate());
    }
}
```

When you restart the application you might see in the logs that the
table is now being create with a proper DATE type like this:

```
Hibernate: 
    create table patient (
        id bigint generated by default as identity,
        birth_date date,
        family_name varchar(255),
        given_name varchar(255),
        primary key (id)
    )
```

That is a huge improvement!  If you use other Java 8 Date types, you'll
need a converter for each (I can't believe no one has created a utility
library for these yet).

# Step 10:  Let's write some tests

So far we've not written a lot of code but its still a good idea to get
into the habit of writing unit tests.


