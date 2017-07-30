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


# Reference REST design guides:

- https://github.com/Microsoft/api-guidelines/blob/master/Guidelines.md
- http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api
- https://codeplanet.io/principles-good-restful-api-design/


# Good Blogs I've found on this journey

- https://vladmihalcea.com/
- https://www.petrikainulainen.net/blog/