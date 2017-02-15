---
layout: post
title:  "Bootstrap your Application"
date: 2017-01-02 12:00:00 -0500
categories: spring-boot initializr
---

Start with the [Spring Initializr](http://start.spring.io/){:target="_blank"} web site at
[http://start.spring.io](http://start.spring.io).
  
The Initializr will help you set up your Spring Boot project.  By filling out just a few details,
you can generate and download a zip file that contains (almost) everything you need to get started.
Don't worry, the Initializr is _not_ a code generator in the traditional sense, but more of a
generator for initial project scaffolding.

In the Dependencies field, type in: `web, actuator, jpa, h2` and `devtools`.  If you hit Enter after
each tag is matched, it will be permanently selected.  Feel free to customize the `Group` and
`Artifact` coordinates as you see fit but keep the default Spring Boot Version.
  
Don't be afraid to click "Switch to the full version" and explore the other Spring Boot options.
You may even choose to start your project with a Gradle build configuration instead (however, I will
assume Maven for the remainder of this journal).

Once the dependencies selected, click the the Generate Project button.  When the download finishes,
unzip the file and import the project into your development environment.  I primarily use the
[IntelliJ IDE](https://www.jetbrains.com/idea/) from JetBrains, so some of the steps that I describe
may not match exactly, but they should be close.

**Run the application.**

Depending on your environment, the Run configuration might be automatically configured for you.  If
not, just navigate the project source folders until you find the default Application class and run
it manually.  If everything goes well, you should see several colorful INFO messages logged out to
the console and finally a Tomcat instance listening on the default port of 8080.

Try it out at: [localhost:8080](http://localhost:8080)

You should see the default "White label" error page in your browser.  Don't worry, you haven't done
anything wrong, this is the default Spring Boot error page.  The Initializr only generates just
enough code to get you started and this is the expected result for what we selected.
 
If you use the `curl` command instead, you'll see that it returns a JSON error response.  This is
Spring Boot being smart about the requested Content Type.  By default, Boot will return the most
appropriate Content Type in the response.

Ultimately, we will customize some of this behavior but for now, lets write some code.
