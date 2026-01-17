package com.safalifter.userservice.query;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindUserByUsernameQuery {
    private final String username;
}
