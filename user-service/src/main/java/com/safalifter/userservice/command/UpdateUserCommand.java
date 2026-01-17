package com.safalifter.userservice.command;

import com.safalifter.userservice.model.UserDetails;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class UpdateUserCommand {
    @TargetAggregateIdentifier
    private final String userId;
    private final String username;
    private final String password;
    private final UserDetails userDetails;
}
