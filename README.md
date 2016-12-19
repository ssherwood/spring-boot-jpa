# Spring Boot JPA Reference Application

[![Build Status](https://travis-ci.org/ssherwood/spring-boot-jpa.svg?branch=master)](https://travis-ci.org/ssherwood/spring-boot-jpa)

## Overview

This project is a revisit an older one that I did a few years back when I took part in the
[Coursera](https://www.coursera.org/) Specialization on [Android Development](https://www.coursera.org/specializations/android-app-development).

During the final Capstone we were given the choice of several projects to implement and I chose one that was both
interesting and personal: an application to help cancer patients self-report on their pain symptoms so their doctors
can be notified of extended durations of persistent pain or inability to eat.

A brief article about this specific project was published on the Vanderbilt School of Engineering's
[web site](http://engineering.vanderbilt.edu/news/2014/capstone-app-project-for-mooc-aims-to-track-help-manage-cancer-patients-pain/).

## History

The original server-side implementation of the Capstone project was my first time using Spring Boot and since then I've
felt that there were many ways to improve upon the original code.
  
Since the final Capstone project was only a few weeks long, I did not
get as much time to dedicate to the server-side as I would have liked. I
still had to implement a complete Android front-end for the application
as well (something I had never done).

## Goals

I'd like to re-design the original server-side implementation of the
Capstone project and take this opportunity to document the process so I
can use it as a reference application to share with other developers.

---

# Step 1: "Initializ" the application

As with most Spring Boot apps, start with the [Spring Initializr](http://start.spring.io/)
web site.  In the Dependencies section, type in: `Web, Actuator, JPA, H2, and Devtools`
and click the Generate button (feel free to customize the Group and Artifact
Id as you see fit).

Unzip the downloaded artifact and import the project into the IDE of
your preference.

Next, "Run" the application.  Depending on your IDE, this might be a
right-click command on the Application class that was automatically
generated from the Initializr.

If all goes well, you should see several INFO commands printed out to
the Console and a Tomcat instance being started on port 8080.
  
Try it out now at: (http://localhost:8080).

You should see the default "Whitelablel" error page.  

Don't worry, this is the Spring Boot default error page that is
indicating that you've requested a resource that doesn't exist.  It
doesn't exist because we haven't implemented anything yet.

Side Note: If you use the `curl` command instead of the browser, you'll
get a JSON response instead as Spring Boot is attempting to detect the
origin of the caller and return the most appropriate response type.
Ultimately, we'll create a custom error handler for these scenarios but
for now lets jump into some actual coding.

# Step 2: Start with the Domain

Lets start by creating a domain object.  By domain object, I'm referring
to an object that represents the business entity we're trying to model.
For more information on Domain Modeling, refer to Eric Evans' book on
[Domain Driven Design](https://www.amazon.com/Domain-Driven-Design-Tackling-Complexity-Software/dp/0321125215).

In Java we can use the standard persistence API (called JPA) to
implement this domain object.  These are basic Java classes that have a
special `@Entity` annotation on them.

To create an entity, create a `domain` package under the default
application package.  Then, create a Java class called `Patient` and add
the following code:

```java
@Entity
public class Patient {
    @Id
    @GeneratedValue
    private Long id;
}
```

Restart the application in the IDE.

If you watch the logs closely, you'll see some additional information
being printed out from a library called Hibernate (this is Spring Boot's
default JPA implementation).

What you may not have noticed however is that by including H2 as a
dependency you are also running an in-memory database with a full web
console:
http://localhost:8080/h2-console

FYI: Make sure you set the JDBC URL to 'jdbc:h2:mem:testdb' instead of 
the default or else you won't see the PATIENT table that was create when
we started the app (this is the Spring Boot default database URL).

Sweet.  This is pretty nice but how do we get the application to be able
to create, read, update and delete Patients?

# Step 3: Create a Repository

Create a package called `repositories` in the base package and create a
`PatientRepository` Java interface class.
  
Finally, add the following code:

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
```

That's not a lot of code but what does it do?

By extending the JpaRepository class we creating a Spring "Repository"
class that is automatically initialized with a JPA entity manager.  Not
only is it automatically initialized, it also has several basic
CRUD-type operations already pre-defined.

TODO: talk about why I'm not using @RestRepositories

Before we get too far ahead of ourselves, lets enable some basic
configuration options that will help with debugging.  Edit the default
`application.properties` file in the src/main/resources folder and add:

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

I *STRONGLY* recommend adding these properties and keeping them set for
the duration of your local development lifecycle.  You will always want
to review the SQL that is being created and executed on your behalf.

# Step 4: Set up a REST Controller

Create a package called `controllers` in the default package and create
a `PatientController` class within it.  Then add the following code to
the class: 

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

After the application restarted you should now be able to make "RESTful"
calls against the /patient URL like this: (http://localhost:8080/patient/1)

However, you will notice that nothing is displayed... that's weird.

TODO discuss findOne behavior of returning null

# Step 5: Create a FindBy Implementation

Since the default behavior really isn't all that desirable, lets add a
more appropriate method to the Repository interface:

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findById(Long id);
}
```

and change the GET implementation in the controller to:

```java
    @GetMapping("/patient/{id}")
    public Patient getPatient(@PathVariable("id") Long id) {
        return patientRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
    }
```

Apply these changes and restart.

Now if we use curl:

```
curl -sS localhost:8080/patient/1 | jq
```

we will at least get a 500 status error code.  However, that is not the
most desirable response from a REST API.

TODO Discuss Spring's lack of standard http error exceptions and handlers

# Step 6

Create a package called `exceptions` and add a Java class called `NotFoundException`
with the following code:

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

and modify the GET implementation to throw this instead of the IllegalArgumentException:

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

```java
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

```java
    public Long getId() {
        return id;
    }
```

Now when you execute the POST we can see an id field on the JSON object

```bash
curl -sS -H "Content-Type: application/json" -X POST localhost:8080/patient -d '{}' | jq
```

TODO discuss Lombok

# Step 8:  Lets make this Patient more interesting

Add the following attributes to the Patient entity:

```java
    private String givenName;
    private String familyName;
    private LocalDate birthDate;
```

Add the associated getter/setters using your IDEs code generator if
possible.

Using curl again submit a Patient add request:

```bash
curl -sS -H "Content-Type: application/json" -X POST localhost:8080/patient -d '{"givenName":"Max","familyName":"Colorado","birthDate":"1942-12-11"}' | jq
```

400 Error?  So that LocalDate is causes a JsonMappingException.

We need to add a Jackson dependency to the project so it knows how to
handle JSR 310 Dates (introduced in Java 8).  Add this to the pom.xml:

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>

```

Refresh the dependencies and restart the application.  Now we see an odd
looking date structure:

```json
"birthDate": [
    1942,
    12,
    11
  ]
```

That isn't exactly what we want.
  
There is yet another configuration change we need here to tell Jackson
to format the date "correctly".  Update the application.properties and
include:

```
spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS = false
```

Volia!

But wait.  If we look at how the date is actually stored in the database
through the H2 console, it looks like a BLOB.  That isn't good since
we might want to be able to query against it later.

Why doesn't JPA support LocalDate?

# Step 9: Create a JPA Converter

As of the time of writing, JPA still does not natively support the JSR
310 dates.  It does, however, provide support for custom converters that
can.

Add a package called `converters` and add a class called `LocalDateAttributeConverter`.
 Then add the following code:
 
```java
@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    public Date convertToDatabaseColumn(LocalDate localDate) {
        return (localDate == null ? null : Date.valueOf(localDate));
    }

    @Override
    public LocalDate convertToEntityAttribute(Date sqlDate) {
        return (sqlDate == null ? null : sqlDate.toLocalDate());
    }
}
```

When you restart the application you might see in the logs that the
table is now being create with a proper DATE type like this:

```sql
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
into the habit of writing unit tests.  Spring Boot 1.4 provides some new
testing capabilities that we want to take advantage of.

First, lets create a test for our PatientRepository.  In the test section,
create a package called `repositories` and then add a Java class called
`PatientRepositoryTests`.  In that class add the following code:

```java
@RunWith(SpringRunner.class)
@DataJpaTest
public class PatientRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    PatientRepository patientRepository;

    @Test
    public void test_PatientRepository_FindById_ExpectExists() throws Exception {
        Long patientId = entityManager.persistAndGetId(new Patient(), Long.class);
        Patient aPatient = patientRepository.findById(patientId).orElseThrow(NotFoundException::new);
        assertThat(aPatient.getId()).isEqualTo(patientId);
    }
}
```

Run the test and review the logs.  You should see several SQL statements
being executed by the JPA provider.  In this case we see the PATIENT table
CREATE, INSERT, SELECT and finally DROP.

Specifically with the INSERT and SELECT statements, is this similar to
what you might have written by hand?

Spoiler Alert!  I expect that this test case will fail at some point in
the future.  Can you guess why?

Now let's add a test for the Controller.  Create a package called
`controllers` and create a test class called `PatientControllerTests`.
Then add the following code:

```java
@RunWith(SpringRunner.class)
@WebMvcTest(PatientController.class)
public class PatientControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientRepository mockPatientRepository;

    @Test
    public void test_MockPatient_Expect_ThatGuy() throws Exception {
        given(mockPatientRepository.findById(1L)).willReturn(Optional.of(thatGuy()));

        mockMvc.perform(get("/patient/1")
                .accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.givenName", is("Guy")))
                .andExpect(jsonPath("$.familyName", is("Stromboli")))
                .andExpect(jsonPath("$.birthDate", is("1942-11-21")));
    }

    private Patient thatGuy() {
        Patient aPatient = new Patient();
        aPatient.setGivenName("Guy");
        aPatient.setFamilyName("Stromboli");
        aPatient.setBirthDate(LocalDate.of(1942, 11, 21));
        return aPatient;
    }
}
```

This is a "mock" test.  It is not testing the repository that we created
but instead is leveraging a mock version of it thanks to Mockito.

Basically, all we are doing is telling the mock how to behave when we
call it and then are asserting that it is responding correctly.  It is a
good test but it isn't really a complete test end-to-end test.  The
overall benefit of this kind of test is when you want to test certain
behaviors that might be hard to recreate under normal circumstances.

Next, lets add a more complete web test.  In the same package, add a
test class called `PatientControllerWebTests`.  Then add the following
code:

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT)
public class PatientControllerWebTests {

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
    public void test_PatientController_getPatient_Expect_Patient99999999_NotFound() throws Exception {
        ResponseEntity<Patient> entity = restTemplate.getForEntity("/patient/99999999", Patient.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void test_PatientController_getPatient_Expect_Invalid() throws Exception {
        String response = restTemplate.getForObject("/patient/foo", String.class);
        assertThat(response).contains("Bad Request");
    }
}
```

If you run this test right now, it will fail.  Why?
  
Well, the obvious answer is that when we ran the test and the database
is initialized with empty data.  How do we initialize the database so we
can rely on a specific data set?

One way is to tap into a feature of Spring Boot.  If we create a file
in the test section of the project called `resources` and then a file
called `data.sql` within it we have a way to initialize the database on
ever test case.

Create that file and add the following to it:

```sql
INSERT INTO PATIENT(id, given_name, family_name, birth_date) VALUES (null, 'Phillip', 'Spec', '1972-5-5');
INSERT INTO PATIENT(id, given_name, family_name, birth_date) VALUES (null, 'Sally', 'Certify', '1973-6-6');
```

During the startup of the tests, Spring Boot will invoke this file and
initialize the database.  Now, if we re-run the test cases, they should
succeed with a green bar!

Yeah!

# Step 11:  What about performance?

TODO setup a performance test.
https://github.com/jmeter-maven-plugin/jmeter-maven-plugin
http://www.xoriant.com/blog/software-testing-and-qa/performance-testing-of-restful-apis-using-jmeter.html

TODO I've add an initial performance plan but this will need to be
better documented as JMeter can be a complex tool in and of itself.
Additionally, I'm still working out details on how to load the in-memory
database using a CSV so I can share the data between the application and
the JMeter test (what I have appears to work but I'm still looking for
better that having to store the CSV in the resources/classpath).

After this, it might be a good idea to go back and refactor this to be
the pattern used from the start.

FYI - The reason I'm wanting to have JMeter set up so early in the life
of the project is so that we can establish base-lines for later as we
add more complex capabilities.  I think its a good idea to have some
sense of what impact to performance a specific feature can have.

Another piece to document would be to connect to the application with
JConsole and then run the test.  Reviewing heap and threads is a good
practice.

Finally, consider introducing the jmeter plugin to the maven POM.

# Step 12: Validation JSR 309/3

There a a few types of validations that we can add to our application
for the entity classes.
  
One type is to use the JPA `@Column` annotations to define constraints
on the resulting database tables.  I typically like to delay this
exercise until the project is a bit more fleshed out.  During this
initial discovery phase, I find it more advantageous to leave the schema
more flexible and adaptable.  At some point in the future I plan on
having the schema be versioned - then we'll need to investigate
additional tooling like Flyway or Liquibase.

Another option for data validation is the Java Bean Validator
annotations:
https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#table-spec-constraints

In the domain object for Patient, lets add a few common sense
validations:

```java
    @NotBlank @Size(min = 2)
    private String givenName;
    @NotBlank @Size(min = 2)
    private String familyName;
```

If you restart the application and look at the Hibernate create table,
you'll see that I was wrong.  Hibernate did honor these validations and
modified the schema accordingly.  Now if I try to post a blank JSON
object to the /patient URL, I should see an error:

```json
{
  "timestamp": "2016-12-14T15:22:39.519+0000",
  "status": 500,
  "error": "Internal Server Error",
  "exception": "javax.validation.ConstraintViolationException",
  "message": "Validation failed for classes [com.undertree.symptom.domain.Patient] during persist time for groups [javax.validation.groups.Default, ]\nList of constraint violations:[\n\tConstraintViolationImpl{interpolatedMessage='may not be null', propertyPath=givenName, rootBeanClass=class com.undertree.symptom.domain.Patient, messageTemplate='{javax.validation.constraints.NotNull.message}'}\n\tConstraintViolationImpl{interpolatedMessage='may not be null', propertyPath=familyName, rootBeanClass=class com.undertree.symptom.domain.Patient, messageTemplate='{javax.validation.constraints.NotNull.message}'}\n]",
  "path": "/patient"
}
```

During persistence I violated one or more constraints and the database
isn't happy.  I'm not happy either because of the 500 error status code.
I'd actually like to catch this condition higher up the stack and return
a more proper error.

Java and Spring have a simple solution for this.  Add a `@Valid`
annotation to the RequestBody parameter like this:

```java
    @PostMapping("/patient")
    public Patient addPatient(@Valid @RequestBody Patient patient) {
        return patientRepository.save(patient);
    }
```

Restart and rerun the POST:

```json
{
  "timestamp": "2016-12-14T15:41:20.115+0000",
  "status": 400,
  "error": "Bad Request",
  "exception": "org.springframework.web.bind.MethodArgumentNotValidException",
  "errors": [
    {
      "codes": [
        "NotNull.patient.givenName",
        "NotNull.givenName",
        "NotNull.java.lang.String",
        "NotNull"
      ],
      "arguments": [
        {
          "codes": [
            "patient.givenName",
            "givenName"
          ],
          "arguments": null,
          "defaultMessage": "givenName",
          "code": "givenName"
        }
      ],
      "defaultMessage": "may not be null",
      "objectName": "patient",
      "field": "givenName",
      "rejectedValue": null,
      "bindingFailure": false,
      "code": "NotNull"
    },
    {
      "codes": [
        "NotNull.patient.familyName",
        "NotNull.familyName",
        "NotNull.java.lang.String",
        "NotNull"
      ],
      "arguments": [
        {
          "codes": [
            "patient.familyName",
            "familyName"
          ],
          "arguments": null,
          "defaultMessage": "familyName",
          "code": "familyName"
        }
      ],
      "defaultMessage": "may not be null",
      "objectName": "patient",
      "field": "familyName",
      "rejectedValue": null,
      "bindingFailure": false,
      "code": "NotNull"
    }
  ],
  "message": "Validation failed for object='patient'. Error count: 2",
  "path": "/patient"
}
```

It 400 error is more in lines with what I was expecting but wow that is
a verbose error.

TODO this would be a good place to look into customizing the Spring Boot
standard error object.  The errors block is just blindly marshalling the
entire object with codes and arguments that aren't all that useful to a
client.  We could probably clean this up a bit.

I did waste some time trying to customized the ValidationMessages and
had a little success but it didn't feel as clean as I would have liked
so I need to do more research there as well.

Lets run or test cases just to be sure everything is working as
expected.  Wait, there was an error:

```
javax.validation.ConstraintViolationException: Validation failed for classes [com.undertree.symptom.domain.Patient] during persist time for groups [javax.validation.groups.Default, ]
List of constraint violations:[
	ConstraintViolationImpl{interpolatedMessage='may not be empty', propertyPath=familyName, rootBeanClass=class com.undertree.symptom.domain.Patient, messageTemplate='{org.hibernate.validator.constraints.NotBlank.message}'}
	ConstraintViolationImpl{interpolatedMessage='may not be empty', propertyPath=givenName, rootBeanClass=class com.undertree.symptom.domain.Patient, messageTemplate='{org.hibernate.validator.constraints.NotBlank.message}'}
]
```

This is what I thought might happen.  Our original test was using an
empty resource during the Add.  Now this is an invalid state.  We should
fix that first.

First add a new library to our dependencies in the pom.xml:

```xml
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-lang3</artifactId>
			<version>3.5</version>
		</dependency>
```

We'll use this in conjunction with a helpful utility class that can be
added to test suite under a new `domain` package.  This will be a new
Java class called `TestPatientBuilder` with the code:

```java
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
```

This class lets us create random test patients with the added ability to
override any attribute that we would like.  This can be very useful in
unit testing.

Now, try it out buy updating the failing test case:

```java
   @Test
    public void test_PatientRepository_FindById_ExpectExists() throws Exception {
        Long patientId = entityManager.persistAndGetId(new TestPatientBuilder().build(), Long.class);
        Patient aPatient = patientRepository.findById(patientId).orElseThrow(NotFoundException::new);
        assertThat(aPatient.getId()).isEqualTo(patientId);
    }
```

We should go ahead and add a few negative tests that certify the
validations that we recently added.  First add a JUnit rule for
exceptions:

```java
    @Rule
    public ExpectedException thrown = ExpectedException.none();
```

Now when we expect an exception to be thrown we'll set up the `thrown`
with our expectations:

```java
    @Test
    public void test_PatientRepository_SaveWithNull_ExpectException() throws Exception {
        thrown.expect(InvalidDataAccessApiUsageException.class);
        patientRepository.save((Patient)null);
    }

    @Test
    public void test_PatientRepository_SaveWithEmpty_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(containsString("'may not be empty'"));
        patientRepository.save(new Patient());
    }

    @Test
    public void test_PatientRepository_SaveWithEmptyGivenName_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(allOf(containsString("givenName"), containsString("'may not be empty'")));
        patientRepository.save(new TestPatientBuilder().withGivenName("").build());
    }

    @Test
    public void test_PatientRepository_SaveWithEmptyFamilyName_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(allOf(containsString("familyName"), containsString("'may not be empty'")));
        patientRepository.save(new TestPatientBuilder().withFamilyName("").build());
    }

    @Test
    public void test_PatientRepository_SaveWithShortGivenName_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(allOf(containsString("givenName"), containsString("'size must be between 2 and")));
        patientRepository.save(new TestPatientBuilder().withGivenName("A").build());
    }

    @Test
    public void test_PatientRepository_SaveWithShortFamilyName_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(allOf(containsString("familyName"), containsString("'size must be between 2 and")));
        patientRepository.save(new TestPatientBuilder().withFamilyName("Z").build());
    }
```

Not only can we assert that a specific exception is thrown but we can
verify that it contains the message details that we would expect.

Finally, lets refactor and update the Controller tests.  In the
`PatientControllerTests` refactor to use the `TestPatientBuilder`:

```java
        given(mockPatientRepository.findById(1L))
                .willReturn(Optional.of(new TestPatientBuilder()
                        .withGivenName("Guy")
                        .withFamilyName("Stromboli")
                        .withBirthDate(LocalDate.of(1942, 11, 21))
                        .build()));
```

In the `PatientControllerWebTests` add a few new tests that also verify
the `@Valid` is working:

```java
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
```

# Step 13 - More Attributions

The patient needs some more attribution.  Let's add a few new fields that will help round out the `Patient`:

```java
    private String additionalName;
    @Transient
    private Integer age;
    @Email
    private String email;
    @Min(0)
    private Short height; // height in cm
    @Min(0)
    private Short weight; // weight in kg
```

The `additionalName` is useful for capturing a person's middle but what is the @Transient annotation on `age`?  Well
this is a derived attribute based on `birthDate`.  Since it is derived, we don't want JPA to persist it so this
annotation is useful for signifying that (its basically an ignore marker).  However, since we don't have any storage
mechanism for this how will it get set?

I've chosen to calculate the Patient's age each time `birthDate` is set.  Like this:

```java
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        this.age = Period.between(birthDate, LocalDate.now()).getYears();
    }
```

This ensures that any time the birthDate is modified the age is recalculated.  However, if you run the program right now
age will always appear null.  That is a bit of a surprise.  It is because Hibernate is using Field level access based on
how we've used the JPA annotations.  There is a simple way to update the birthDate field so Hibernate will instead use
Property-level access:

```java
    @Access(AccessType.PROPERTY)
    private LocalDate birthDate;
```

Now Hibernate will use the getter/setter methods when accessing birthDate and age will get set appropriately.

In addition add the associated getter and setter for the new properties and restart the application.  Note: since age is
not stored we don't want anyone to accidentally change it.  An easy way to ensure this is to not provide a setter method
for it.

Our new properties are being returned as null in the response JSON.  If we don't want Jackson to marshal null values,
there is a quick setting that we can change in the `application.properties`:

```properties
spring.jackson.default-property-inclusion=non_null
```

That looks better.  If we want, we can go ahead and update the patients.csv to support these new fields:

```csv
ID,BIRTH_DATE,GIVEN_NAME,FAMILY_NAME,ADDITIONAL_NAME,GENDER,EMAIL,HEIGHT,WEIGHT
1,1972-05-05,Phillip,Spec,J,1,pjspec@junit.org,84,180
2,1973-06-06,Sally,Certify,T,2,,78,163
3,1962-02-15,Frank,Neubus,,0,,88,178
```

Then update the associate `data.sql`:

```sql
INSERT INTO PATIENT(ID,BIRTH_DATE,GIVEN_NAME,FAMILY_NAME,ADDITIONAL_NAME,GENDER,EMAIL,HEIGHT,WEIGHT)
  (SELECT * FROM CSVREAD('classpath:patients.csv'));
```

We also need to look at our test cases but first, lets update the `TestPatientBuilder` to account for the new
attributes:

```java
        testPatient.setEmail(String.format("%s@%s.com", RandomStringUtils.randomAlphanumeric(20),
                RandomStringUtils.randomAlphanumeric(20)));
        testPatient.setGender(Gender.values()[RandomUtils.nextInt(0, Gender.values().length)]);
        testPatient.setHeight((short) RandomUtils.nextInt(140, 300));
        testPatient.setWeight((short) RandomUtils.nextInt(50, 90));
```

Don't forget to add the associate "with" methods so we can override the random values if needed.

```java
    public TestPatientBuilder withAdditionalName(String additionalName) {
        testPatient.setAdditionalName(additionalName);
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
```

With the builder in place add some additional tests to the PatientRepositoryTests to exercise some of the new
constraints:

```java
    @Test
    public void test_PatientRepository_SaveWithInvalidEmail_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(allOf(containsString("email"), containsString("'not a well-formed email address'")));
        patientRepository.save(new TestPatientBuilder().withEmail("baz").build());
    }

    @Test
    public void test_PatientRepository_SaveWithLessThanMinHeight_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(allOf(containsString("height"), containsString("'must be greater than or equal to 0'")));
        patientRepository.save(new TestPatientBuilder().withHeight((short) -1).build());
    }

    @Test
    public void test_PatientRepository_SaveWithLessThanMinWeight_ExpectException() throws Exception {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage(allOf(containsString("weight"), containsString("'must be greater than or equal to 0'")));
        patientRepository.save(new TestPatientBuilder().withWeight((short) -1).build());
    }
```

Add a few more tests to the PatientControllerWebTests class:

```java
    @Test
    public void test_PatientController_addPatient_WithInvalidEmail_Expect_BadRequest() throws Exception {
        ResponseEntity<String> json = restTemplate.postForEntity("/patient",
                new TestPatientBuilder().withEmail("bad/email").build(), String.class);

        assertThat(json.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JSONAssert.assertEquals("{exception:\"org.springframework.web.bind.MethodArgumentNotValidException\"}", json.getBody(), false);
    }

        @Test
    public void test_PatientController_addPatient_WithBirthDate_Expect_ValidAge() throws Exception {
        ResponseEntity<Patient> entity = restTemplate.postForEntity("/patient",
                new TestPatientBuilder().withBirthDate(LocalDate.of(1980, 1, 1)).build(), Patient.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).isNotNull()
                .hasFieldOrPropertyWithValue("age", LocalDate.now().getYear() - 1980);
    }
```



---

## TODOs
- Add more @Valid
- Add more example mocking tests
- Add equals/hashcodes
- Add better performance tests
- Add Query by Example examples
- Add QueryDsl support
- Add custom response wrapping
- Add support for Flyway
- Add support for JPA/JTA transactions
- Add REST documentation Swagger vs RESTDocs
- Add Spring Security with OAuth2 and JWT
- Explain HIPA and PII concerns


# Additional Resources

- [Spring Boot Reference Guide]:(https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA]:(http://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Hibernate ORM]:(http://hibernate.org/orm/)
- [Hibernate Validator]:(http://hibernate.org/validator/)
- [Jackson]:(http://wiki.fasterxml.com/JacksonHome)
- [H2 Database]:(http://www.h2database.com/html/main.html)


# License

   Copyright 2016 Shawn Sherwood

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.