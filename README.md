# Driver and Fleet Management System

My Spring Boot application for managing drivers and vehicles in a fleet management system. This RESTful API provides comprehensive functionality for driver and vehicle management, including assignment, status tracking, and search capabilities.



## Features

### Driver Management
- Create, read, update, and delete driver profiles
- Manage driver status (ACTIVE, ON_LEAVE, SUSPENDED, INACTIVE)
- Search drivers by various criteria (name, license type, status)
- Track license information and validate against vehicle types

### Vehicle Management
- Create, read, update, and delete vehicle records
- Manage vehicle status (AVAILABLE, IN_USE, IN_SERVICE, OUT_OF_ORDER)
- Search vehicles by various criteria (brand, model, type, status)
- Track mileage and technical inspection dates

### Relationship Management
- Assign vehicles to drivers
- Validate license types against vehicle types
- Prevent invalid assignments (suspended drivers, out-of-order vehicles)
- Enforce business rules (license compatibility, status restrictions)

### Asynchronous Notifications
- Automatic detection of vehicles with upcoming technical inspections
- Configurable time range for checking inspection dates

### API Documentation
- Open API
- Swagger UI for API exploration and testing



## Technical Features

### Core Framework
- **Java 23**: Latest Java version
- **Spring Boot 3.4.4**: Enterprise application framework
- **Maven**: Build and dependency management

### Data & Persistence
- **Spring Data JPA**: Data access with Hibernate ORM
- **PostgreSQL**: Production database with connection pooling
- **H2**: In-memory database for development and testing
- **Flyway**: Database migrations with environment-specific scripts

### Performance & Optimization
- **Caffeine Caching**: In-memory cache with 15-minute TTL, 500 item capacity
- **Entity Graphs**: N+1 query problem prevention
- **Database Indexing**: Optimized queries for frequent lookups
- **Pagination**: All list endpoints support pagination and sorting

### Security & Authentication
- **JWT Authentication**: Token-based auth with role-based access control
- **Spring Security**: Comprehensive security configuration
- **Password Encryption**: BCrypt hashing for stored passwords
- **Rate Limiting **: Resilience4j for restricting the number of requests allowed within a specific time

### Code Quality & Mapping
- **MapStruct**: Compile-time DTO mapping generation
- **Lombok**: Boilerplate code reduction
- **Jakarta Validation**: Comprehensive input validation with custom validators
- **AOP Logging**: Aspect-oriented logging for all service operations

### Testing & Documentation
- **Comprehensive Tests**: Unit and integration tests with high coverage
- **OpenAPI 3**: Complete API documentation with Swagger UI


### Monitoring & Operations
- **Spring Actuator**: Health checks, metrics, and operational endpoints
- **Prometheus Integration**: Custom metrics for business operations
- **Cache Statistics**: Real-time cache hit/miss ratios and performance metrics
- **Application Health**: Database connectivity and system status monitoring

### DevOps & Deployment
- **Docker**: Multi-stage builds with optimized container images
- **Docker Compose**: Complete stack deployment (app + database + Jenkins)
- **Jenkins CI/CD**: Automated build, test, and deployment pipeline
- **Multi-Environment**: Separate configurations for dev/test/prod environments

### Asynchronous Processing
- **Background Notifications**: Inspection reminder processing runs asynchronously
- **Thread Pool Management**: Dedicated executor (2-5 threads) for background tasks  
- **Non-blocking Response**: API returns immediately while notifications process in background
- **CompletableFuture**: Async notification handling with proper error management




## Profiles
- Critical: Each profile uses different Flyway migration paths. Choose specific profile to avoid database schema conflicts.
- The application supports multiple environment profiles:
- dev: Uses H2 in-memory database with dedicated Flyway migrations (classpath:db/migration/h2) and DEBUG level logging (detailed SQL, Flyway, app logs)
- test: Uses H2 for testing with appropriate test configurations (classpath:db/migration/h2) and DEBUG level logging for comprehensive test debugging
- prod: Uses PostgreSQL with production-specific Flyway migrations (classpath:db/migration/postgresql) and WARN/INFO level logging for minimal production output



## Jenkins Setup
- Start all the container: docker-compose up -d
- Access jenkins: http://localhost:8090
- Get initial admin password: docker exec driver-and-fleet-management-application-jenkins-1 cat /var/jenkins_home/secrets/initialAdminPassword
- Complete the setup process
- Install additional plugins:
- Configure a new pipeline


## Project Structure

- Configuration classes for Spring Boot, Security, Cache, and OpenAPI
- REST controllers with comprehensive API endpoints
- Data transfer objects (DTOs) for data exchange
- Custom exceptions and global exception handling
- MapStruct mappers for entity-DTO conversion
- JPA entities with proper relationships
- Spring Data repositories
- Service layer with business logic implementation
- Utility classes for common functionality
- Tests

Resources include:
- Flyway migration scripts in separate folders for H2 and PostgreSQL
- Environment-specific application properties (dev, test, prod)

Other:
-Dockerfile
-Docker-compose.yml



## Getting Started

### Prerequisites
- JDK 
- Maven 
- Docker and Docker Compose (for containerized deployment)

### Environment Profiles
The application supports multiple environment profiles:
- **dev**: Uses H2 in-memory database with dedicated Flyway migrations
- **test**: Uses H2 for testing with appropriate test configurations
- **prod**: Uses PostgreSQL with production-specific Flyway migrations

### Docker Deployment
1. Build the Docker image"
   
   docker-compose build

2. Start the containers:

   docker-compose up -d

3. Stop the containers

   docker-compose down



## API Endpoints

### Driver Endpoints
- `GET /api/drivers`: Get all drivers
- `GET /api/drivers/{id}`: Get driver by ID
- `GET /api/drivers/license/{licenseNumber}`: Get driver by license number
- `GET /api/drivers/status/{status}`: Get drivers by status
- `GET /api/drivers/search`: Search drivers by name
- `GET /api/drivers/license-type/{licenseType}`: Get drivers by license type
- `POST /api/drivers`: Create a new driver
- `PUT /api/drivers/{id}`: Update a driver
- `DELETE /api/drivers/{id}`: Delete a driver
- `PATCH /api/drivers/{id}/status`: Update driver status


### Vehicle Endpoints
- `GET /api/vehicles`: Get all vehicles
- `GET /api/vehicles/{id}`: Get vehicle by ID
- `GET /api/vehicles/plate/{licensePlate}`: Get vehicle by license plate
- `GET /api/vehicles/status/{status}`: Get vehicles by status
- `GET /api/vehicles/search`: Search vehicles by brand and model
- `GET /api/vehicles/type/{type}`: Get vehicles by type
- `GET /api/vehicles/driver/{driverId}`: Get vehicles by driver ID
- `POST /api/vehicles`: Create a new vehicle
- `PUT /api/vehicles/{id}`: Update a vehicle
- `DELETE /api/vehicles/{id}`: Delete a vehicle
- `PATCH /api/vehicles/{id}/mileage`: Update vehicle mileage
- `PATCH /api/vehicles/{id}/status`: Update vehicle status


### Notification Endpoints
- `POST /api/notifications/inspection-reminders`: Send notifications for vehicles with upcoming technical inspections


### Authentication Endpoints
- `POST /api/auth/login`: Authenticate and receive JWT token


### Assignment Endpoints
- `POST /api/assignments/{driverId}/vehicle/{vehicleId}`: Assign vehicle to driver
- `DELETE /api/assignments/{driverId}/vehicle/{vehicleId}`: Remove vehicle from driver


### Monitoring Endpoints
- `GET /actuator/health`: Application health status
- `GET /actuator/prometheus`: Metrics in Prometheus format
- `GET /actuator/caches`: Cache statistics

  

## Security

The API implements JWT authentication with two predefined users:
- `user`: Regular user with read-only access (password: userpassword)
- `admin`: Administrator with full access (password: adminpassword)



## License

This project is licensed under the MIT License - see the LICENSE file for details.
