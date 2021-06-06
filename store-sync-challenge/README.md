![Status](https://github.com/iundarigun/store-challenge/actions/workflows/sync-ci.yml/badge.svg)
# Challenge Sync Store

Welcome to the store-challenge! This folder contains the code to run a synchronous application to get products from database and consult a list of products with the sku, name, category, price and discounts.

## How to run

We need MockWS, Redis and Postgres to run. I Prepared three ways:
1. All images are getting from docker hub, store challenge too.
```shell
docker-compose -f environment/docker-compose-images.yml up
```

2. MockWS, Postgres and Redis images are getting from docker hub, but store challenge is compiling when start:
```shell
docker-compose -f environment/docker-compose-compiling.yml up
```
_Advice_: These first and second options sometimes fails, because `depends on` instruction doesn't wait for ready connections on database, and application sometimes is ready before database, mainly the first run. If this is your case, stopped and try again.

3. MockWS, Postgres and Redis images are getting from docker hub, and we can run store from IDE. Used to develop time:
```shell
docker-compose -f environment/docker-compose.yml up
```

I add swagger to access: http://localhost:1980/swagger-ui.html

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

I added github actions to run all main commit as CI: https://github.com/iundarigun/store-challenge/actions/workflows/sync-ci.yml

## Technologies
The main technology are `Kotlin` with `Spring Boot`.
- I choose Kotlin because aims me a good developer experience and has some features like `data class`, `null-safe types` and `extension functions` very useful.
- Spring Boot is a powerful tool with a big support for almost anything. The fast project start up is another good reason

- After the first reactive code (on store-coroutines-challenge folder), I realized that for the deadline, if I wanted to show more than basic, I needed to used sync mode, because my experience is bigger than in reactive programming.

- For the database that I choose: `Postgres`. I choose it for many reasons:
    - I am a good experience with it
    - I want to add `Flyway` to project to manage the schema migrations.
    - Good support with JPA Spring Data, better than in reactive mode.

- I also add Redis for cache, using Spring Cache features. In this case, Redis is an over engineering, and Caffeine may be resolve better. But if you have more than one instance of the application, Redis can be a good solution.

- I add an external communication, mocking an endpoint trying to consult the stock of the product on another system, using Feign as HTTP Client. I use MockWS for this -a project was created for me two years ago-. It allows us to add delays and failing calls. For leading with, I used Resilient4j with Retry and CircuitBreaker configuration. I let the window of CircuitBreaker small to see the behavior.

- To Unit test, I choose Junit5 with mockk for mock dependencies.

- To Integration test, I choose to use `RestAssured` with `TestContainers`. The main reason is to have an environment near the real scenario. TestContainers helps us to start containers with Postgres and other dependencies. The `application.yml` on test resource folder has specific properties.

I used `detekt` to keep my code inside the patterns. It is like `Ktlint` but with more flexibility to configure. Exists a task on gradle to run it. 

## Package design
I separated with package with logical layers:
- `client`: Contains client feign definitions
- `controller`: Contains the endpoints.
- `domain`: Contains da entities and DTOs separate in `entity`, `request` and `response` packages
- `exception`: Contains the specific application exception
- `extension`: Contains Kotlin extensions  
- `repository`: Contains the access database layer
- `service`: Contains the business logic. Every entity has a service, who only access to the same entity respository. For access other entity data, use service. 
