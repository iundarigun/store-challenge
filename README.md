![Status](https://github.com/iundarigun/store-challenge/actions/workflows/gradle.yml/badge.svg)

# Product Challenge

Welcome to the store-challenge! This repository contains logic to get products from database and consult a list of products with the sku, name, category, price and discounts.

## How to run

We need Redis and Postgres to run. I Prepared three ways:
1. All images are getting from docker hub, store challenge too. 
```shell
docker-compose -f environment/docker-compose-images.yml up
```

2. Postgres and Redis images are getting from docker hub, but store challenge is compiling when try to start:
```shell
docker-compose -f environment/docker-compose-images.yml up
```

3. Postgres and Redis images are getting from docker hub, and we can run store from IDE. Use to develop time:
```shell
docker-compose -f environment/docker-compose.yml up
```

### To run test

We can run all tests directly from command line. During the process a Postgres container is started:
```shell
./gradlew test
```

We can run only unit test or only integration tests
```shell
./gradlew unitTests
./gradlew integrationTests
```


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

I add too Redis from cache. Since Reactive not support Cache in Spring by annotation, I implement manually and very silly, only to test it. I do not implement for Flow/Flux returns, only for simple object, but for this goal, is really not necessary use Redis.

To Unit test, I choose Junit5 with mockk for mock dependencies. 

To Integration test, I choose to use `RestAssured` with `TestContainers`. The main reason is to have an environment near the real scenario. 
- TestContainers helps us to start containers with Postgres and other dependencies. The `application.yml` on test resource folder has specific properties.
- When the application starts, RestAssured calls endpoints to test.

I used `detekt` to keep my code inside the patterns. It is like `Ktlint` but with more flexibility to configure. Exists a task on gradle to run it. 

I add `Blockhound` to the project to detect blocking execution on runtime. I need to allow some classes for permit `@Transactional` and `swagger`
```kotlin
builder.allowBlockingCallsInside("java.util.UUID", "randomUUID") // @Transactional
                .allowBlockingCallsInside("java.io.InputStream", "readNBytes") // Swagger
                .allowBlockingCallsInside("java.io.FilterInputStream", "read") // Swagger
                .allowBlockingCallsInside("org.springdoc.webflux.api.OpenApiWebfluxResource", "openapiJson") // Swagger
```

## Package design
I separated with package with logical layers:
- `configuration`: Contains configurations like flyway and property files
- `controller`: Contains the endpoints.
- `domain`: Contains da entities and DTOs
- `exception`: Contains the specific application exception
- `repository`: Contains the access database layer
- `service`: Contains the business logic. Every entity has a service, who only access to the same entity respository. For access other entity data, use service. 



Techs:
- [X] Kotlin
- [X] Spring Boot
- [X] Postgres ~~or Mongo?~~
- [X] Flyway
- [X] Reactive ~~or no?~~
- [X] Detailed business rules
- [X] Exception Handler
- [X] Cache Redis
- [ ] Security
- [X] TestsContainers
- [X] Rest assured
- [X] Checkstyle with detekt

- [X] Unit tests
- [X] BlockHound
- [ ] Docker to start without nothing more

Pomodoro cycles:
- 12
