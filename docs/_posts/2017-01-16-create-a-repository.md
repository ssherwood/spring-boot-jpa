---
layout: post
title:  "Create a Repository"
date: 2017-01-16 12:00:00 -0500
categories: jpa spring-data
---

A Repository is another DDD concept that describes an abstraction that mediates the domain model
from data mapping layers.  With Spring Data, we use Repositories to wrap the persistence operations
for a specific Entity keeping the underlying details out of the domain.

To create a Repository for our Patient, first create a package called `repositories` in the base
package and create a `PatientRepository` Java interface class.  Finally, add the following code to
the interface:

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
```

Surprisingly, that's not a lot of code.  But what does it actually do?

By extending the JpaRepository class we are creating a Spring Data "Repository" class that is
automatically initialized with a JPA entity manager.  It also has several basic CRUD operations
already provided.

// TODO: talk about why I'm not using @RestRepositories - in short, I find Spring Data REST to be
a little too opinionated and harder to customize for certain situations.

Before we get too far ahead of ourselves, lets enable some basic configuration options that will
help with future debugging: edit the default `application.properties` file in the
`src/main/resources` folder and add:

```
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

I **STRONGLY** recommend adding these properties and keep them set for the duration of your local
development lifecycle.  You will frequently want to review the SQL that is being created and
executed on your behalf by Hibernate.
