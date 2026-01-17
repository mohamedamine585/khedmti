# User Service - CQRS/Event Sourcing with Axon Framework

This project has been converted from a traditional CRUD architecture to use CQRS (Command Query Responsibility Segregation) and Event Sourcing with Axon Framework and Axon Server.

## Architecture Overview

### Components

1. **Axon Server**: Event Store and Message Router (running on localhost:8124)
2. **Command Side**: 
   - Commands: CreateUserCommand, UpdateUserCommand, DeleteUserCommand
   - Aggregate: UserAggregate (handles commands and applies events)
   - Events: UserCreatedEvent, UserUpdatedEvent, UserDeletedEvent

3. **Query Side**:
   - Queries: FindUserByIdQuery, FindUserByEmailQuery, FindUserByUsernameQuery, FindAllUsersQuery
   - Projection: UserProjection (handles events and updates read model)
   - Read Model: PostgreSQL database with JPA repository

### Key Changes

- **User Model**: Still uses JPA for the read model (query side)
- **UserAggregate**: New Axon aggregate that handles commands and emits events
- **Commands**: Separate command objects for all write operations
- **Queries**: Separate query objects for all read operations
- **UserService**: Updated to use CommandGateway and QueryGateway instead of direct repository access
- **UserProjection**: Event handlers that update the read model (PostgreSQL) when events occur

## Prerequisites

- Java 17
- Maven
- Docker and Docker Compose
- PostgreSQL (for read model)
- Axon Server (provided via Docker)

## Setup and Running

### 1. Start Axon Server

```bash
docker-compose -f docker-compose-axon.yml up -d
```

This will start Axon Server on:
- HTTP Dashboard: http://localhost:8024
- gRPC Client Port: localhost:8124

### 2. Configure Database

Make sure PostgreSQL is running and configure the connection in your config server or application.properties:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/userdb
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build and Run the Application

```bash
./mvnw clean install
./mvnw spring-boot:run
```

## How It Works

### Write Operations (Commands)

When a user is created, updated, or deleted:
1. Controller receives HTTP request
2. Service creates a Command object
3. Command is sent via CommandGateway to Axon Server
4. UserAggregate handles the command
5. Aggregate validates and applies an Event
6. Event is stored in Axon Server's Event Store
7. UserProjection receives the event
8. Projection updates the PostgreSQL read model

### Read Operations (Queries)

When user data is requested:
1. Controller receives HTTP request
2. Service creates a Query object
3. Query is sent via QueryGateway
4. UserProjection handles the query
5. Data is retrieved from PostgreSQL read model
6. Result is returned to the controller

## Benefits of This Architecture

1. **Event Sourcing**: Complete audit trail of all changes
2. **CQRS**: Separate read and write models for optimization
3. **Scalability**: Can scale read and write sides independently
4. **Eventual Consistency**: Read model is eventually consistent with write model
5. **Replay Events**: Can rebuild read model from events
6. **Time Travel**: Can reconstruct state at any point in time

## Axon Server Dashboard

Access the Axon Server dashboard at http://localhost:8024 to:
- View all events
- Monitor command/query/event flow
- Inspect aggregates
- View metrics and health

## API Endpoints (Unchanged)

All REST endpoints remain the same:
- POST /v1/user/save - Create user
- GET /v1/user/getAll - Get all active users
- GET /v1/user/getUserById/{id} - Get user by ID
- GET /v1/user/getUserByEmail/{email} - Get user by email
- GET /v1/user/getUserByUsername/{username} - Get user by username
- PUT /v1/user/update - Update user
- DELETE /v1/user/deleteUserById/{id} - Delete user (soft delete)

## Monitoring

The application includes Axon-specific actuator endpoints for monitoring:
- Command bus metrics
- Query bus metrics
- Event processor status
- Aggregate status

Access via: http://localhost:8081/actuator

## Troubleshooting

### Axon Server Connection Issues
- Ensure Axon Server is running: `docker ps`
- Check logs: `docker logs axonserver`
- Verify port 8124 is not blocked

### Event Store Issues
- View events in Axon Server dashboard
- Check projection handler logs
- Verify PostgreSQL connection for read model

### Replay Events
To rebuild the read model from events, you can use Axon Server's replay functionality through the dashboard.

## Notes

- The application still uses PostgreSQL for the **read model** (queries)
- Axon Server handles the **event store** (commands and events)
- This is a hybrid approach: Event Sourcing for writes, traditional DB for reads
- The repository is still used, but only by the projection handlers for the query model
