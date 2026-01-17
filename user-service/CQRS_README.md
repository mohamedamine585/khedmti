# CQRS with Axon Server Implementation

## Overview
This project has been refactored to implement CQRS (Command Query Responsibility Segregation) pattern using Axon Framework and Axon Server.

## Architecture

### Command Side (Write Model)
- **Commands**: `CreateUserCommand`, `UpdateUserCommand`, `DeleteUserCommand`
- **Aggregate**: `UserAggregate` - handles commands and applies events
- **Events**: `UserCreatedEvent`, `UserUpdatedEvent`, `UserDeletedEvent`

### Query Side (Read Model)
- **Queries**: `FindAllUsersQuery`, `FindUserByIdQuery`, `FindUserByEmailQuery`, `FindUserByUsernameQuery`
- **Query Model**: `UserQueryModel` - JPA entity for reading
- **Query Handlers**: `UserQueryHandler` - handles queries
- **Projection**: `UserProjection` - listens to events and updates query model

### Event Sourcing
- All user state changes are stored as events in Axon Server
- The `UserAggregate` is event-sourced, meaning it can be rebuilt from its event stream
- The query model is eventually consistent with the command side

## Setting Up Axon Server

### Option 1: Docker (Recommended)

```bash
docker run -d --name axonserver -p 8024:8024 -p 8124:8124 axoniq/axonserver
```

### Option 2: Download and Run

1. Download Axon Server from: https://download.axoniq.io/axonserver/AxonServer.zip
2. Unzip the file
3. Run:
   ```bash
   java -jar axonserver.jar
   ```

### Accessing Axon Server Dashboard

Once running, access the dashboard at: http://localhost:8024

The dashboard shows:
- Event Store contents
- Command/Query handlers
- Running applications
- Performance metrics

## Configuration

The application is configured to connect to Axon Server at `localhost:8124` (gRPC port).

You can change this in `application.properties`:
```properties
axon.axonserver.servers=localhost:8124
```

## Key Changes from Original Implementation

1. **No more direct database writes for User entity**: Commands are sent to aggregates
2. **Event-driven**: All state changes produce events
3. **Separate read/write models**: 
   - Write: `UserAggregate` (event-sourced)
   - Read: `UserQueryModel` (JPA entity)
4. **Asynchronous command handling**: Commands return `CompletableFuture`
5. **Eventually consistent queries**: Query model is updated by event handlers

## Benefits

- **Scalability**: Read and write models can be scaled independently
- **Audit Trail**: Complete history of all changes via event store
- **Temporal Queries**: Can rebuild state at any point in time
- **Event-Driven Architecture**: Easy to add new event listeners
- **Performance**: Optimized read models for queries

## Running the Application

1. Start Axon Server (see above)
2. Start your database (PostgreSQL)
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## API Endpoints (unchanged)

All REST endpoints remain the same:
- `POST /v1/user/save` - Create user
- `GET /v1/user/getAll` - Get all users
- `GET /v1/user/getUserById/{id}` - Get user by ID
- `GET /v1/user/getUserByEmail/{email}` - Get user by email
- `GET /v1/user/getUserByUsername/{username}` - Get user by username
- `PUT /v1/user/update` - Update user
- `DELETE /v1/user/deleteUserById/{id}` - Delete (soft delete) user

## Monitoring Events

You can view all events in Axon Server dashboard:
1. Go to http://localhost:8024
2. Click on "Search" in the left menu
3. Select your application context
4. View event streams for each aggregate

## Testing

The original `UserService` is still available if needed for migration purposes. The new CQRS implementation is in `UserCommandService`.

## Troubleshooting

### Cannot connect to Axon Server
- Ensure Axon Server is running on port 8124
- Check firewall settings
- Verify the configuration in `application.properties`

### Events not appearing in query model
- Check `UserProjection` logs
- Verify database connection
- Check Axon Server dashboard for event processing errors

## Notes

- The old `User` JPA entity is kept for reference but is no longer used
- The `UserRepository` is replaced by `UserQueryRepository` for the query model
- Passwords are still hashed before storing in commands
- Soft delete is maintained (setting `Active.INACTIVE` instead of physical delete)
