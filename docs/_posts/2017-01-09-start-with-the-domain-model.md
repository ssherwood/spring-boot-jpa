---
layout: post
title:  "Start with the Domain Model"
date: 2017-01-09 12:00:00 -0500
categories: domain-drive-design jpa h2
---

Lets start by creating a domain object.  By a domain object, I'm referring to an object that closely
represents a business entity that we're trying to model.  For more information on Domain Modeling, 
please refer to Eric Evans' excellent book on
[Domain Driven Design](https://www.amazon.com/Domain-Driven-Design-Tackling-Complexity-Software/dp/0321125215).

In Java we can use the standard persistence API (also called JPA) to further implement this domain
object.  These are basic Java data classes that have a special `@Entity` annotation on them.  To
create an entity, create a `domain` package under the default application package.  Then, create a
Java class called `Patient` and add the following code:

```java
@Entity
public class Patient {
    @Id
    @GeneratedValue
    private Long id;
}
```

Restart the application in the IDE.

If you watch the logs closely, you'll see some additional information being printed out from a
library called Hibernate (this is Spring Boot's default JPA implementation).

What you may not have noticed however is that by including H2 library as a dependency you are also
now running an in-memory database with a full web [console](http://localhost:8080/h2-console).

// TODO: this isn't correct, you have to enable it with the property `spring.h2.console.enabled=true`
and I'm not entirely sure why mine is enable by default...

FYI: Make sure you set the JDBC URL to 'jdbc:h2:mem:testdb' instead of the default or else you
won't see the PATIENT table that was create when we started the app (this is the Spring Boot default
database URL).

Sweet!  This is pretty nice but how do we get the application to be able to Create, Read, Update
and Delete our Patients?