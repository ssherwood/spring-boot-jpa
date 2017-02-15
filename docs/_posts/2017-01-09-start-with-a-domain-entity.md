---
layout: post
title:  "Start with the Domain"
date: 2017-01-09 12:00:00 -0500
categories: domain-driven-design ddd jpa hibernate h2
---

Lets creating a domain object.  By a domain object, I mean an object that we will use to represent
the business entity that we want to model.  To do this, we will use a design technique called
Domain-Driven Design.  The Wikipedia entry for
[Domain-Driven Design](https://en.wikipedia.org/wiki/Domain-driven_design) states:
                                                            
> Domain-driven design (DDD) is an approach to software development for complex needs by connecting the implementation to an evolving model.

That sounds quite abstract, but in practice, it will become apparent that DDD is just a set of
concepts that are technology agnostic and will help provide a ubiquitous language that can be used
to help solve many types of complex problems.
  
You are not required to be an expert in DDD, but it does help to understand the basics.  The
Wikipedia article above is a great start, but for more information, please refer to Eric Evans'
excellent book: [Domain-Driven Design: Tackling Complexity in the Heart of Software](https://www.amazon.com/Domain-Driven-Design-Tackling-Complexity-Software/dp/0321125215).
Or, for the more impatient reader [Domain-Driven Design Distilled](https://www.amazon.com/Domain-Driven-Design-Distilled-Vaughn-Vernon/dp/0134434420)
by Vaughn Vernon.

## The Patient Entity

In DDD, an Entity is a special type of domain object that has a unique, unchangeable identity that
when combined with mutable attributes, can be used to express a model.
  
In the the domain of the Pain Management application, we need to represent a Patient.  This would
appear to map well to the `@Entity` annotation defined by the standard
[Java Persistence API](https://en.wikipedia.org/wiki/Java_Persistence_API) (JPA).  However, it is
important to note that, from a design perspective, we should resist coupling an Entity with the
(sometimes) cumbersome details of the underlying storage technology.

Initially, don't worry about schemas, columns or indexes.  We will deal with those concerns when it
becomes appropriate.  For now, just worry about the details of the identity and attributes that we
need to define a Patient.

To create the Patient Entity, first create a `domain` package under the default package name that
you used for the project.  Then create a new Java Class called `Patient` and add the following code:

```java
@Entity
public class Patient {
    @Id
    @GeneratedValue
    private Long id;
}
```

This is really the simplest Entity that we could define using JPA.
  
The Patient's identity (id) is a synthetic value that is arguably a poor choice for representing the
concept long term.  However, the choice is intentional as it is an immediate convenience but we will
be able to refactor it later.

DDD "purists" may dislike this approach, arguing that the JPA annotations immediately couple the
technology to the domain.  In general, I tend to be more pragmatic on this topic, particularly when
using Spring Data as it helps balance design and development with the right level of abstraction and
still get things done.

**Save and restart the application.**

This time, if you watch the logs closely, you should see additional messages from Hibernate.
Hibernate is Spring Boot's default JPA implementation and, because we have an @Entity defined, it is
able to act on our behalf.  In fact, Hibernate automatically defined an initial schema and created a
database table for us, but how?

When we originally created the project, we chose the `H2` database and, by including it's
dependency, Spring Boot will automatically activated it in-memory during local development.
Hibernate uses H2 and will execute the commands to create a table per the @Entity definition.  This
is a very fast and convenient way to get your design ideas realized without having to immediately
fuss with storage details.

H2 also comes with a very handy [web console](http://localhost:8080/h2-console) that Spring
Boot can launch.  If your application does not have the console, you may need to add a property to
enable it in the default `application.properties` file:

```
spring.h2.console.enabled=true
```

I have noticed that the H2 console is automatically enabled if you also have the `devtools`
dependency in your project (this may be unintended so be aware of the configuration option above).
Also, make sure you set the JDBC URL to `jdbc:h2:mem:testdb` instead of the console default or you
won't see the PATIENT table.

As mentioned earlier, do not get too concerned with the underlying database technology.  For the
time being, we will let Hibernate decide how best to define and manage the database schema.  In this
way we initially get the flexibility and agility that can come with using so called "schemaless"
systems.

For now lets focus on the basics of interacting with the Patient entity.
