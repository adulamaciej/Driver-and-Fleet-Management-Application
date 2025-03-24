# Driver-and-Vehicle-Management-Application

A Spring Boot application designed for managing drivers and vehicles in a fleet management system.


Overview:
This application provides a comprehensive API for fleet administration, allowing users to manage drivers and vehicles, assign vehicles to drivers, track vehicle status, and more.

Driver Management:
Create, retrieve, update, and delete driver information
Search drivers by various criteria (ID, license number, status, name)
Manage driver status (active, on leave, suspended, inactive)
Track driver's license information



Vehicle Management:
Create, retrieve, update, and delete vehicle information
Search vehicles by various criteria (ID, license plate, status, brand/model)
Track vehicle status (available, in use, in service, out of order)
Monitor technical inspection dates and mileage



Relationship Management:
Assign vehicles to drivers (with validation based on license type)
Remove vehicle assignments
View vehicles assigned to a specific driver




Technical Stack:
Java
Maven
Spring Boot
Spring Data JPA
Spring Security
H2 Database
Flyway
Lombok
Mockito
JUnit
Springdoc OpenAPI
MapStruct
Caffeine



Maven
