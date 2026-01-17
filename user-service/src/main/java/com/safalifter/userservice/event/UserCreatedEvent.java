package com.safalifter.userservice.event;

import com.safalifter.userservice.enums.Active;
import com.safalifter.userservice.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private String userId;
    private String username;
    private String password;
    private String email;
    private Role role;
    private Active active;
}
