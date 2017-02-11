# Spring Boot / Spring Data JPA: A Reference Application

[![Build Status](https://travis-ci.org/ssherwood/spring-boot-jpa.svg)](https://travis-ci.org/ssherwood/spring-boot-jpa)
[![Dependency Status](https://www.versioneye.com/user/projects/589f261a940b23003d2b00fc/badge.svg)](https://www.versioneye.com/user/projects/589f261a940b23003d2b00fc)
[![License](https://img.shields.io/badge/license-Apache%20License%202.0-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

## Overview and History

This project is a revisit of one that I did a few years back in 2014 when I took part in the
[Coursera](https://www.coursera.org/) Specialization on [Android Development](https://www.coursera.org/specializations/android-app-development).

During the final Capstone class, we were given the choice of several projects to implement and I
chose one that was both interesting and had a personal connection: an application to help cancer
patients self-report on their pain symptoms so that their doctors could be notified of extended
durations of persistent pain or the inability to eat.  In theory, this could help doctors to
directly interact with their patients much more quickly and hopefully identify issues before
letting them get out of control.

A brief article about the original project was published on the Vanderbilt School of Engineering's
[web site](http://engineering.vanderbilt.edu/news/2014/capstone-app-project-for-mooc-aims-to-track-help-manage-cancer-patients-pain/).

My original server-side implementation of the Capstone project was also my first real interaction
with Spring Boot and since then, I've always felt that there many improvements that I could have
made.

## Goals

I'd like to take the time to re-design the original implementation and document the process so that
I can use it as a reference application for myself and to share with other Spring developers.

*WARNING* This means that this is still a work-in-progress, so you may find things broken or
incomplete as I get time to work on fleshing out the essential use-cases.

---

FYI -
I'm gradually moving content here: [https://ssherwood.github.io/spring-boot-jpa/](https://ssherwood.github.io/spring-boot-jpa/)

---

## TODOs

- Add custom @Valid
- Add more example mocking tests (when and why)
- Add equals/hashcodes
- Add better performance tests
- Add QueryDsl support
- Add Optimistic Locking support with @Version
- Add more data to the patients.csv
- Add custom response wrapping
- Add support for Flyway
- Add support for JPA/JTA transactions
- Add REST documentation Swagger vs RESTDocs (http://apihandyman.io/categories/posts/)
- Add Spring Security with OAuth2 and JWT
- Add Auditing
- Explain HIPA and PII concerns
- Convert to Kotlin?
- Discuss this JPA approach benefits has initial schema-less feel
- Research https://github.com/FasterXML/jackson-datatype-hibernate
- Google style https://github.com/google/styleguide
- Research QuerydslBinderCustomizer
- Research Json Patch vs Http Patch vs my naive implementation 
  - https://stackoverflow.com/questions/36907723/how-to-do-patch-properly-in-strongly-typed-languages-based-on-spring-example?rq=1


# Additional Resources

- [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](http://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [QueryDsl](http://www.querydsl.com/)
- [Hibernate ORM](http://hibernate.org/orm/)
- [Hibernate Validator](http://hibernate.org/validator/)
- [Jackson](http://wiki.fasterxml.com/JacksonHome)
- [H2 Database](http://www.h2database.com/html/main.html)
- [AssertjJ](https://joel-costigliola.github.io/assertj/)

# Reference REST design guides:

- https://github.com/Microsoft/api-guidelines/blob/master/Guidelines.md
- http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api
- https://codeplanet.io/principles-good-restful-api-design/


# Good Blogs I've found on this journey

- https://vladmihalcea.com/
- https://www.petrikainulainen.net/blog/

# License

    Copyright 2016-2017 Shawn Sherwood

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
