---
layout: post
title:  "Start with a Domain Entity"
date: 2017-01-09 12:00:00 -0500
categories: domain-driven-design ddd jpa hibernate h2
---

Lets start by creating a domain object.  By a domain object, I'm referring to an object that closely
represents the business entity that we're trying to model.  For more information on Domain Modeling, 
please refer to Eric Evans' excellent book on
[Domain Driven Design](https://www.amazon.com/Domain-Driven-Design-Tackling-Complexity-Software/dp/0321125215).

In Domain Driven Design (DDD) an Entity is a type of object that has a unique unchangeable identity
that can be used to express a model.  In the the Pain Management application, we have identified the
need to represent a Patient.  This would seem to map fairly well to the @Entity annotation defined
by the standard Java Persistence API (JPA).

However, it is important to not overly couple the design of an Entity too closely to the backing
data storage technology.  Initially, don't worry about defining schemas, columns or indexes as those
concerns will be worked out later.

To create the Patient entity: create a `domain` package under the default application package.
Then, create a Java class called `Patient` and add the following code:

```java
@Entity
public class Patient {
    @Id
    @GeneratedValue
    private Long id;
}
```

This is the simplest Entity that we could possibly define.  The Patient identity is a purely
synthetic value and is arguably a poor choice for any long term representation of a Patient.
However, it does provide a quick and convenient way to initially manage the Patient as we flesh out
the underlying design.

FYI: Purists of DDD may dislike this approach arguing that the JPA annotations couple the
technology to the domain.  I tend to be more pragmatic and have found that it balances many of the
benefits of DDD with the reality of development in the Java ecosystem.

**Save and restart the application.**

This time, if you watch the logs closely, you'll see additional information being printed out from
the Hibernate library (this is Spring Boot's default JPA implementation).

When we originally generated the project, we also selected `H2` and, by including that dependency,
Spring Boot activates an in-memory H2 database for development use.  Additionally, H2 also has a
very useful [web console](http://localhost:8080/h2-console).

If your application does not automatically have the console running, you may need to add a property
to enable it in the application.properties file: `spring.h2.console.enabled=true`.  I have noticed
that the console is automatically enabled if you also have the devtools dependency, but this may be
an unintended side effect.

Make sure you set the JDBC URL to `jdbc:h2:mem:testdb` instead of the default or you won't see the
PATIENT table that was created when we started the application.  As mentioned earlier, do not get
too concerned about the underlying database.  Initially, we will let Hibernate decide how to define
the schema so we get the flexibility and agility that comes with so called "schema-less" systems.

The next question is, "How do we get the application to be able to Create, Read, Update and Delete
our Patient?"