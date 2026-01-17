package com.safalifter.userservice.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDeletedEvent {
    private final String userId;
}
