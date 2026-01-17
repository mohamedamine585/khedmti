package com.safalifter.userservice.projection;

import com.safalifter.userservice.enums.Active;
import com.safalifter.userservice.enums.Role;
import com.safalifter.userservice.event.UserCreatedEvent;
import com.safalifter.userservice.event.UserDeletedEvent;
import com.safalifter.userservice.event.UserUpdatedEvent;
import com.safalifter.userservice.exc.NotFoundException;
import com.safalifter.userservice.model.User;
import com.safalifter.userservice.query.FindAllUsersQuery;
import com.safalifter.userservice.query.FindUserByEmailQuery;
import com.safalifter.userservice.query.FindUserByIdQuery;
import com.safalifter.userservice.query.FindUserByUsernameQuery;
import com.safalifter.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserProjection {

    private final UserRepository userRepository;

    @EventHandler
    public void on(UserCreatedEvent event) {
        log.info("Handling UserCreatedEvent for userId: {}", event.getUserId());
        
        // Check if user already exists by ID or email (idempotent handling)
        if (userRepository.findById(event.getUserId()).isPresent()) {
            log.info("User already exists in projection by ID, skipping event for userId: {}", event.getUserId());
            return;
        }
        
        if (userRepository.findByEmail(event.getEmail()).isPresent()) {
            log.warn("User already exists in projection by email, skipping event for userId: {} email: {}", 
                    event.getUserId(), event.getEmail());
            return;
        }
        
        try {
            User user = User.builder()
                    .username(event.getUsername())
                    .password(event.getPassword())
                    .email(event.getEmail())
                    .role(event.getRole())
                    .active(event.getActive())
                    .build();
            
            // Set the ID directly now that BaseEntity has a setter
            user.setId(event.getUserId());
            
            userRepository.save(user);
            log.info("Successfully created user in projection: {}", event.getUserId());
        } catch (Exception e) {
            // Handle duplicate key or any other constraint violation gracefully
            log.warn("Failed to save user (possibly duplicate), skipping: userId={}, email={}, error={}", 
                    event.getUserId(), event.getEmail(), e.getMessage());
        }
    }

    @EventHandler
    public void on(UserUpdatedEvent event) {
        log.info("Handling UserUpdatedEvent for userId: {}", event.getUserId());
        User user = userRepository.findById(event.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        if (event.getUsername() != null) {
            user.setUsername(event.getUsername());
        }
        if (event.getPassword() != null) {
            user.setPassword(event.getPassword());
        }
        if (event.getUserDetails() != null) {
            user.setUserDetails(event.getUserDetails());
        }
        
        userRepository.save(user);
    }

    @EventHandler
    public void on(UserDeletedEvent event) {
        log.info("Handling UserDeletedEvent for userId: {}", event.getUserId());
        User user = userRepository.findById(event.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        user.setActive(Active.INACTIVE);
        userRepository.save(user);
    }

    @QueryHandler
    public User handle(FindUserByIdQuery query) {
        log.info("Handling FindUserByIdQuery for userId: {}", query.getUserId());
        return userRepository.findById(query.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @QueryHandler
    public User handle(FindUserByEmailQuery query) {
        log.info("Handling FindUserByEmailQuery for email: {}", query.getEmail());
        return userRepository.findByEmail(query.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @QueryHandler
    public User handle(FindUserByUsernameQuery query) {
        log.info("Handling FindUserByUsernameQuery for username: {}", query.getUsername());
        return userRepository.findByUsername(query.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @QueryHandler
    public List<User> handle(FindAllUsersQuery query) {
        log.info("Handling FindAllUsersQuery");
        return userRepository.findAllByActive(Active.ACTIVE);
    }
}
