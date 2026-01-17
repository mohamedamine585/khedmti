package com.safalifter.userservice.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class DeleteUserCommand {
    @TargetAggregateIdentifier
    private final String userId;
}
