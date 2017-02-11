---
layout: post
title:  "Bootstrap your Application"
date: 2017-01-02 12:00:00 -0500
categories: spring-boot initializr
---

As with many Spring Boot applications, the first place to go is the [Spring Initializr](http://start.spring.io/){:target="_blank"}
web site.
  
The Initializr will help set up your Spring Boot application.  By just filling out just a few
project specific details, you will be able to download a zip file that contains (almost) everything
needed to get started.  Don't worry, the Initializr is not a not a code generator in the traditional
sense, but more of a project generator.

In the Dependencies field, type in: `web, actuator, jpa, h2` and `devtools`.  Once you have these
items selected, click the the large Generate Project button.  Feel free to customize the `Group`
and `Artifact` coordinates as you see fit.

At the time of writing, the current version of Spring Boot is 1.4.3.  In general pick the latest GA
version of Spring Boot for your project.  You can also choose the generate the project with a Gradle
build configuration but this write-up will assume Maven.

Unzip the downloaded file and import the project into the development environment of your choice.
I primarily use the [IntelliJ IDE](https://www.jetbrains.com/idea/) from JetBrains, so the steps
that I describe may not be match exactly, but they should be close.

Now "Run" the application.  Depending on your IDE, this might be automatically configured or you
may have to locate the main Application class generated by Initializr and right-click to run it.  If
all goes well (and it usually should), you should see several colorful INFO commands printed out to
the console and a default Tomcat instance started on port 8080.

Try it out now: [localhost:8080](http://localhost:8080)

You should see the default "White label" error page.  Don't worry, this is the Spring Boot default
error page that is indicating that you've requested a resource that doesn't yet exist.  It doesn't
exist because we haven't implemented anything yet.

Side Note: If you use the `curl` command instead of the browser, you'll get a JSON response instead
as Spring Boot is attempting to detect the origin of the caller and return the most appropriate
response type.

Ultimately, we'll create a custom error handler for these scenarios but for now, lets jump in and do
some actual coding.