package com.safalifter.userservice.command;

import com.safalifter.userservice.enums.Active;
import com.safalifter.userservice.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserCommand {
    @TargetAggregateIdentifier
    private String userId;
    private String username;
    private String password;
    private String email;
    private Role role;
    private Active active;
}
