---
layout: post
title:  "Create a JPA Repository"
date: 2017-01-09 12:00:00 -0500
categories: jpa spring-data
---

Create a package called `repositories` in the base package and create a `PatientRepository` Java
interface class.  Finally, add the following code:

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
```

That's not a lot of code but what does it actually do?

By extending the JpaRepository class we are creating a Spring "Repository" class that is
automatically initialized with a JPA entity manager.  Not only is it automatically initialized, it
also has several basic CRUD-type operations already provided.

// TODO: talk about why I'm not using @RestRepositories - in short, I find Spring Data REST to be
too opinionated

Before we get too far ahead of ourselves, lets enable some basic configuration options that will
help with future debugging: edit the default `application.properties` file in the
`src/main/resources` folder and add:

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

I *STRONGLY* recommend adding these properties and keeping them set for the duration of your local
development lifecycle.  You will want to frequently review the SQL that is being created and
executed on your behalf.
