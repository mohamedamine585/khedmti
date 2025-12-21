package com.safalifter.authservice.service;

import com.safalifter.authservice.client.UserServiceClient;
import com.safalifter.authservice.dto.RegisterDto;
import com.safalifter.authservice.dto.TokenDto;
import com.safalifter.authservice.exc.WrongCredentialsException;
import com.safalifter.authservice.request.LoginRequest;
import com.safalifter.authservice.request.RegisterRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker; 
import io.github.resilience4j.retry.annotation.Retry; 
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserServiceClient userServiceClient;
    private final JwtService jwtService;

    public TokenDto login(LoginRequest request) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        if (authenticate.isAuthenticated())
            return TokenDto
                    .builder()
                    .token(jwtService.generateToken(request.getUsername()))
                    .build();
        else throw new WrongCredentialsException("Wrong credentials");
    }

    
    @CircuitBreaker(name = "userService", fallbackMethod = "registerFallback")
    @Retry(name = "userService")
    public RegisterDto register(RegisterRequest request) {
        return userServiceClient.save(request).getBody();
    }

    // Méthode de secours (Fallback)
    // Elle doit avoir la même signature + l'argument Exception
    public RegisterDto registerFallback(RegisterRequest request, Exception e) {
        return RegisterDto.builder()
                .username("Service temporairement indisponible")
                .email("Veuillez réessayer plus tard")
                .build();
    }
}
