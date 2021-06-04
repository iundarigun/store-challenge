![Status](https://github.com/iundarigun/store-challenge/actions/workflows/gradle.yml/badge.svg)

# Product Challenge

Welcome to the store-challenge! This repository contains logic to get products from database and consult a list of products with the sku, name, category, price and discounts.

## Technologies
The main technology are `Kotlin` with `Spring Boot`. 
- I choose Kotlin because aims me a good developer experience and has some features like `data class`, `null-safe types` and `extension functions` very useful.
- Spring Boot is a powerful tool with a big support for almost anything. The fast project start up is another good reason

The next decision was if the API will be reactive or synchronous. 
- I choose reactive with coroutines because I want to test it. I like the way that works, but I am not sure if is the best decision for this challenge. 
- Mainly, for the database that I choose: `Postgres`. I choose it for many reasons:
    - I am a good experience with it
    - I want to add `Flyway` to project to manage the schema migrations.
    - I considered use MonoDB, but my experience with it is only PoC. I feel little weird use a document oriented database for the schema flexibility and use a strongly typed programming language
    - Finally, I feel postgres weird too because doesn't exist JPA or good alternatives to manage relationships on Spring Data. I preferred to use spring Data that make all Queries manually. 

To Integration test, I choose to use `RestAssured` with `TestContainers`. The main reason is to have an environment near the real scenario. 
- TestContainers helps us to start containers with Postgres and other dependencies. The `application.yml` on test resource folder has specific properties.
- When the application starts, RestAssured calls endpoints to test.

I used `detekt` to keep my code inside the patterns. It is like `Ktlint` but with more flexibility to configure. Exists a task on gradle to run it. 

```kotlin
builder.allowBlockingCallsInside("java.util.UUID", "randomUUID") // @Transactional
                .allowBlockingCallsInside("java.io.InputStream", "readNBytes") // Swagger
                .allowBlockingCallsInside("java.io.FilterInputStream", "read") // Swagger
```

## Layers design

## How to run


Techs:
- [X] Kotlin
- [X] Spring Boot
- [X] Postgres ~~or Mongo?~~
- [X] Flyway
- [X] Reactive ~~or no?~~
- [X] Detailed business rules
- [ ] Extra like add discount/add product?
- [ ] Exception Handler - review some methods
- [ ] Cache Redis
- [ ] Security
- [X] TestsContainers
- [X] Rest assured
- [X] Checkstyle with detekt
- [ ] Unit tests
- [ ] BlockHound
- [ ] Docker to start without nothing more

Pomodoro cycles:
- 10
