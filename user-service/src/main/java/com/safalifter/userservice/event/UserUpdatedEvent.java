package com.safalifter.userservice.event;

import com.safalifter.userservice.model.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatedEvent {
    private String userId;
    private String username;
    private String password;
    private UserDetails userDetails;
}
