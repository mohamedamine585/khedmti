package com.safalifter.userservice.event;

import com.safalifter.userservice.model.UserDetails;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdatedEvent {
    private final String userId;
    private final String username;
    private final String password;
    private final UserDetails userDetails;
}
