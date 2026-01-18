package com.safalifter.userservice.aggregate;

import com.safalifter.userservice.command.CreateUserCommand;
import com.safalifter.userservice.command.DeleteUserCommand;
import com.safalifter.userservice.command.UpdateUserCommand;
import com.safalifter.userservice.enums.Active;
import com.safalifter.userservice.enums.Role;
import com.safalifter.userservice.event.UserCreatedEvent;
import com.safalifter.userservice.event.UserDeletedEvent;
import com.safalifter.userservice.event.UserUpdatedEvent;
import com.safalifter.userservice.model.UserDetails;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
public class UserAggregate {

    @AggregateIdentifier
    private String userId;
    private String username;
    private String password;
    private String email;
    private Role role;
    private Active active;
    private UserDetails userDetails;

    @CommandHandler
    public UserAggregate(CreateUserCommand command) {
        // Validation logic can be added here
        AggregateLifecycle.apply(UserCreatedEvent.builder()
                .userId(command.getUserId())
                .username(command.getUsername())
                .password(command.getPassword())
                .email(command.getEmail())
                .role(command.getRole())
                .active(command.getActive())
                .build());
    }

    @CommandHandler
    public void handle(UpdateUserCommand command) {
        AggregateLifecycle.apply(UserUpdatedEvent.builder()
                .userId(command.getUserId())
                .username(command.getUsername())
                .password(command.getPassword())
                .userDetails(command.getUserDetails())
                .build());
    }

    @CommandHandler
    public void handle(DeleteUserCommand command) {
        AggregateLifecycle.apply(UserDeletedEvent.builder()
                .userId(command.getUserId())
                .build());
    }

    @EventSourcingHandler
    public void on(UserCreatedEvent event) {
        this.userId = event.getUserId();
        this.username = event.getUsername();
        this.password = event.getPassword();
        this.email = event.getEmail();
        this.role = event.getRole();
        this.active = event.getActive();
    }

    @EventSourcingHandler
    public void on(UserUpdatedEvent event) {
        this.username = event.getUsername();
        this.password = event.getPassword();
        this.userDetails = event.getUserDetails();
    }

    @EventSourcingHandler
    public void on(UserDeletedEvent event) {
        this.active = Active.INACTIVE;
    }
}
