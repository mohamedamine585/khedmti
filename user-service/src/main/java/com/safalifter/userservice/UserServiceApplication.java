package com.safalifter.userservice;

import com.safalifter.userservice.command.CreateUserCommand;
import com.safalifter.userservice.enums.Active;
import com.safalifter.userservice.enums.Role;
import com.safalifter.userservice.exc.NotFoundException;
import com.safalifter.userservice.query.FindUserByUsernameQuery;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.UUID;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class UserServiceApplication implements CommandLineRunner {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public UserServiceApplication(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        final String pass = "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa";
        
        try {
            queryGateway.query(new FindUserByUsernameQuery("admin"), Object.class).join();
        } catch (Exception e) {
            // Admin user doesn't exist, create it
           CreateUserCommand command = CreateUserCommand.builder()
                    .userId(UUID.randomUUID().toString())
                    .username("admin")
                    .email("admin@gmail.com")
                    .password(pass)
                    .role(Role.ADMIN)
                    .active(Active.ACTIVE)
                    .build();
            commandGateway.sendAndWait(command);
        }
    }
}
