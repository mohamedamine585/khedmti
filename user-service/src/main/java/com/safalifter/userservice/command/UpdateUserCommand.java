package com.safalifter.userservice.command;

import com.safalifter.userservice.model.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserCommand {
    @TargetAggregateIdentifier
    private String userId;
    private String username;
    private String password;
    private UserDetails userDetails;
}
