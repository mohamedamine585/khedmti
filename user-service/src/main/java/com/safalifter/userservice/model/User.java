package com.safalifter.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.safalifter.userservice.enums.Active;
import com.safalifter.userservice.enums.Role;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseEntity implements Serializable {
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false, updatable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Active active;

    @Embedded
    private UserDetails userDetails;
}
