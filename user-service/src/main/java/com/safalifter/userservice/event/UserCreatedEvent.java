package com.safalifter.userservice.event;

import com.safalifter.userservice.enums.Active;
import com.safalifter.userservice.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreatedEvent {
    private final String userId;
    private final String username;
    private final String password;
    private final String email;
    private final Role role;
    private final Active active;
}
