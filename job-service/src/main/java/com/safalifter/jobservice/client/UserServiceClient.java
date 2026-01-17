package com.safalifter.jobservice.client;

import com.safalifter.jobservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service",url = "http://localhost:8081", path = "/v1/user")
public interface UserServiceClient {
    @GetMapping("/getUserById/{id}")
    ResponseEntity<UserDto> getUserById(@PathVariable String id);
}
