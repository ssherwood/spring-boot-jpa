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

## My Development Journal

- [Bootstrap the Application](http://undertree.io/spring-boot-jpa/bootstrap-your-application)
- [Start with the Domain](http://undertree.io/spring-boot-jpa/start-with-a-domain-entity)
- [Create a Repository](http://undertree.io/spring-boot-jpa/create-a-repository)
- [Create a REST Controller](http://undertree.io/spring-boot-jpa/create-a-rest-controller)
- [Add a Patient via POST](http://undertree.io/spring-boot-jpa/add-patient-via-post-method)
- [Extending the Patient Model (Part 1)](http://undertree.io/spring-boot-jpa/extending-patient-model-1)
- [Extending the Patient Model (Part 2)](http://undertree.io/spring-boot-jpa/extending-patient-model-2)
- TBD

# Additional Resources

- [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](http://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [QueryDsl](http://www.querydsl.com/)
- [Hibernate ORM](http://hibernate.org/orm/)
- [Hibernate Validator](http://hibernate.org/validator/)
- [Jackson](http://wiki.fasterxml.com/JacksonHome)
- [H2 Database](http://www.h2database.com/html/main.html)
- [AssertjJ](https://joel-costigliola.github.io/assertj/)

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
