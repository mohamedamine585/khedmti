package com.safalifter.userservice.query;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindUserByEmailQuery {
    private final String email;
}
