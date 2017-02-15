---
layout: post
title:  "Create a Repository"
date: 2017-01-16 12:00:00 -0500
categories: jpa spring-data
---

A Repository is another DDD concept that is used to describe an abstraction that mediates the domain
model from data mapping layers.

With Spring Data, we use an `@Repository` annotation to wrap common persistence operations for a
specific Entity type and, hopefully, keep the underlying storage details out of the domain.

To create a Patient Repository, first create a package called `repositories` in the base
package and then create a `PatientRepository` Java interface.  Finally, add the following code to
the interface:

```java
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
```

Surprisingly, that's not much code.  But what does it actually do?

By extending the JpaRepository class we are creating a custom Spring Data Repository for the Patient
Entity that will be initialized with a JPA entity manager.  It is a powerful abstraction in Spring
Data that also provides several default CRUD operations and a custom query system that we will
discuss later in more detail.

When Spring manages the Repository, it can generate JPA queries based on the Entity definition that
it is typed to and we will rarely have to deal with JPA directly.

// TODO: talk about why I'm not using @RestRepositories.  In short, I find Spring Data REST to be
too opinionated and harder to customize.  

Before we get too far ahead of ourselves, lets enable some basic configuration options that will
help with future debugging:
 
Edit the default `application.properties` file in the `src/main/resources` folder and add:

```
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

I do **STRONGLY** recommend adding these properties and keeping them set for the duration of your
local development.  You will frequently need to review the SQL that is being created and executed by
Hibernate.
