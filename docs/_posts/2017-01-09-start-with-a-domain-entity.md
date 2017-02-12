---
layout: post
title:  "Start with the Domain"
date: 2017-01-09 12:00:00 -0500
categories: domain-driven-design ddd jpa hibernate h2
---

Lets start by creating a domain object.  By domain object, I'm referring to an object that
will represent the business entity that we need to model.  To do this, we will use a modeling
technique called Domain-Driven Design.  The Wikipedia entry for
[Domain-Driven Design](https://en.wikipedia.org/wiki/Domain-driven_design) states:
                                                            
> Domain-driven design (DDD) is an approach to software development for complex needs by connecting the implementation to an evolving model.

That sounds quite abstract, but in practice it will become apparent that DDD itself is a set of
concepts that are technology agnostic and will provide a ubiquitous language that can be applied to
many different types of problems and environments.  For more information on DDD, please refer to
Eric Evans' excellent book: [Domain-Driven Design: Tackling Complexity in the Heart of Software](https://www.amazon.com/Domain-Driven-Design-Tackling-Complexity-Software/dp/0321125215).
Or, for the impatient [Domain-Driven Design Distilled](https://www.amazon.com/Domain-Driven-Design-Distilled-Vaughn-Vernon/dp/0134434420)
by Vaughn Vernon.

## The Patient Entity

An Entity is a special type of domain object that has a unique, unchangeable identity and when
combined with its mutable attributes, can be used to express a model.  In the the domain of the Pain
Management application, we will need to represent a Patient.

This concept would appear to map fairly well to the `@Entity` annotation defined by the standard
[Java Persistence API](https://en.wikipedia.org/wiki/Java_Persistence_API) (JPA).  However, it is
important to note that, from a design perspective, we should resist coupling the Entity with the
sometimes cumbersome details of the backing storage technology.

Initially, don't worry about defining schemas, columns or indexes -- those concerns will be worked
much out later.  Just work out the details of the identity and attributes that will model the
domain.

To create the Patient Entity:
create a `domain` package under the default application package.
Then create a new Java Class called `Patient` and add the following code:

```java
@Entity
public class Patient {
    @Id
    @GeneratedValue
    private Long id;
}
```

This is the simplest Entity that we could possibly define in JPA.  The Patient's identity (id) is a
purely synthetic value and is arguably a very poor choice for any long term representation of a
Patient.  Don't worry, we will refactor this later.  This choice provides a quick and convenient way
to initially manage the Patient as we flesh out the underlying design.

FYI: DDS "purists" may dislike this approach, arguing that the JPA annotations couple the technology
to the domain.  I tend to be more pragmatic, particularly when using Spring Boot and Spring Data as
they help to balance the benefits of DDD with the reality of modern development in the Java
ecosystem.

**Save and restart the application.**

If you watch the logs closely, you should see additional information being printed out from the
Hibernate library.  Hibernate is Spring Boot's default JPA implementation and, because we have an
@Entity defined, it is now doing backend work on our behalf.  In fact, Hibernate is defining a
schema and creating a database table for us, but how?

When we originally generated the project, we also selected `H2` and, by including that dependency,
this allows Spring Boot to automatically activate it as an in-memory database for development.
Hibernate was then auto-configured to use H2 and starts executing the commands to initialize the
tables per the @Entity definitions found within the project.

Additionally, H2 also has a very useful [web console](http://localhost:8080/h2-console) that Spring
Boot can automatically start up.  If your application does not have the console running, you may
need to add a property to enable it in the default `application.properties` file:

```
spring.h2.console.enabled=true
```
  
I have noticed that the console is automatically enabled if you also have the devtools dependency,
but this may be an unintended side effect.

Make sure you set the JDBC URL to `jdbc:h2:mem:testdb` instead of the default or you won't see the
PATIENT table that was created when we started the application.  As mentioned earlier, do not get
too concerned about the underlying database.  Initially, we will let Hibernate decide how to define
the schema so we get the flexibility and agility that comes with so called "schema-less" systems.

As mentioned earlier, don't get too caught up on the fact there is a relational database backing the
model, these are implementation details that we will address later.  For now lets focus on the
basics of interacting with the Patient model.