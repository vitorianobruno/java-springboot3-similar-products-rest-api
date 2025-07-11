# SPRING BOOT 3 Similar Products API REST

![](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)
![](https://img.shields.io/badge/SpringBoot-3-green)

Spring Boot 3 Rest API that shows similar products related to the currently one.


You can start the mocks containers and other needed infrastructure following this
repository instructions: https://github.com/dalogax/backendDevTest

```
                   +---------------------+
                   |        TEST         |
                   |---------------------|
                   | GET /product/{id}/  |
                   |      similar        |
                   +----------+----------+
                              |
                              | localhost:5000
                              v
                   +---------------------+
                   |       API REST      |
                   +----------+----------+
                              |
                              | localhost:3001
                              v
                   +---------------------+
                   |        MOCKS        |
                   |---------------------|
                   | GET /product/{id}/  |
                   |     similarids      |
                   | GET /product/{id}   |
                   +---------------------+
```

### :bookmark_tabs: Swagger Documentation
Swagger UI: http://localhost:5000/swagger-ui.html

OpenAPI JSON: http://localhost:5000/v3/api-docs

### :coffee: Compiling
`> mvn clean install`

### :heavy_check_mark: Testing
`> mvn test`

### :rocket: Execution
`mvn spring-boot:run`
