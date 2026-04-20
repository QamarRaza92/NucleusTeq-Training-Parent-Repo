# TODO App(Task Manager)
I built this TODO application which can be used to post some Todo(tasks). We can get the tasks, post a new one, update, patch or delete one.

## What I Used
- Java 17
- Spring Boot
- Spring Data JPA
- H2 Database (in memory)
- Maven
- JUnit 5
- Mockito


## How To Run This Project
1. Open the project in IntelliJ or VS Code
2. Open terminal inside project folder
3. Run this command to run the project:
   mvn spring-boot:run
4. Then the app will start on port 8080


## API Endpoints that I Created To Interact With My Project
GET    /todos                    - to get all tasks at once
GET    /todos/{id}               - to get one task by id
POST   /todos                    - to add new task
PUT    /todos/{id}               - to just update an already existing task
DELETE /todos/{id}               - to delete a task with specified id
PATCH  /todos/toggleStatus/{id}  - to change status of a todo(pending/completed)


## Example Of Request Body For POST and PUT
{
  "title": "Complete Java Assignment",
  "description": "Write all code as per assignment guidelines and do proper testing before pushing on github"
}


## Example Of a dummy Response
{
  "id": 1,
  "title": "Complete Java Assignment",
  "status": "PENDING"
}


## Browser Interaction
After running the app locally, open the browser and go to the following endpoint:
http://localhost:8080/

Use these details:
JDBC URL: jdbc:h2:mem:testdb
Username: qamar
Password: (leave empty)

## How To Run Tests
mvn test

## How To See Test Coverage Report
mvn clean test
mvn jacoco:report

Then open this file in browser:
target/site/jacoco/index.html

## Project Structure
src/main/java/com/springadvanced/taskmanager/
  controller/     - all rest api endpoints
  service/        - every business logic
  repository/     - all database operations
  entity/         - database table (TodoEntity)
  dto/           - data transfer objects to interact without disturbing actual entity(TodoEntity)
  exception/      - custom exceptions and error handling for the project

## Features That I Implemented
- I used constructor injection only (no field injection)
- I used dto layer with manual mapping (no mapstruct)
- I used global exception handling using @ControllerAdvice
- I used logging using slf4j in controller and service layers
- I used unit tests using junit and mockito to test all the project code
- Also notification service client called when todo is created

## All assignment guilines that has been met
- Use of @Entity, @Id, @Table
- Use of @Valid for validation
- DTO implementation and Manual DTO Mapping
- Use of JpaRepository
- Min commit requirement has been met

## Author
Qamar Raza

## Date
April 2026