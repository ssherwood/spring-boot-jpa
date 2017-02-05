---
layout: post
title:  "Bootstrap your Application with Initializr"
date: 2017-01-02 12:00:00 -0500
categories: spring-boot initializr
---

As with many Spring Boot applications, start with the [Spring Initializr](http://start.spring.io/)
web site.

In the Dependencies section, type in: `web, actuator, jpa, h2` and `devtools` and click the Generate
button (feel free to customize the Group and Artifact Ids as you see fit).  At the time of this
writing, the current stable version of Spring Boot is 1.4.3.  In general pick the latest GA version
for your project.

Unzip the downloaded artifact and import the project into the development environment of your
choice.  I primarily use the IntelliJ IDE so the steps that I describe may not be exactly as you
might use but they should be close.

Next, "Run" the application.  Depending on your IDE, this might be a right-click command on the
Application class that was automatically generated from the Initializr.

If all goes well (and it usually should), you should see several INFO commands printed out to the
Console and a Tomcat instance started on port 8080.

Try it out now at: [localhost:8080](http://localhost:8080)

You should see the default "White label" error page.  Don't worry, this is the Spring Boot default
error page that is indicating that you've requested a resource that doesn't yet exist.  It doesn't
exist because we haven't implemented anything yet.

Side Note: If you use the `curl` command instead of the browser, you'll get a JSON response instead
as Spring Boot is attempting to detect the origin of the caller and return the most appropriate
response type.

Ultimately, we'll create a custom error handler for these scenarios but for now, lets jump into
some actual coding.