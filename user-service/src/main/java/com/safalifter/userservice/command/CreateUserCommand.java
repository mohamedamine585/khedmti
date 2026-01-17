package com.safalifter.userservice.command;

import com.safalifter.userservice.enums.Active;
import com.safalifter.userservice.enums.Role;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class CreateUserCommand {
    @TargetAggregateIdentifier
    private final String userId;
    private final String username;
    private final String password;
    private final String email;
    private final Role role;
    private final Active active;
}
