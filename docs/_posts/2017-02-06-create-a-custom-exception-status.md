---
layout: post
title:  "Create a Custom Exception with a @ResponseStatus"
date: 2017-02-06 12:00:00 -0500
categories: spring rest
---

Create a package called `exceptions` and add a Java class called `NotFoundException`
with the following code:

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super();
    }
    
    public NotFoundException(String message) {
        super(message);
    }
}
```

And modify the GET implementation to throw this instead of the IllegalArgumentException:

```java
        return patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Patient %d not found", id)));
```

Restart the application and attempt the `curl` command again.

```json
{
  "timestamp": 1481488691203,
  "status": 404,
  "error": "Not Found",
  "exception": "com.undertree.symptom.exceptions.NotFoundException",
  "message": "Patient 1 not found",
  "path": "/patients/1"
}
```

That looks much better!