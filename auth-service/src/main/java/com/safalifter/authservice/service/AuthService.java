package com.safalifter.authservice.service;

import com.safalifter.authservice.client.UserServiceClient;
import com.safalifter.authservice.dto.RegisterDto;
import com.safalifter.authservice.dto.TokenDto;
import com.safalifter.authservice.exc.WrongCredentialsException;
import com.safalifter.authservice.request.LoginRequest;
import com.safalifter.authservice.request.RegisterRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker; 
import io.github.resilience4j.retry.annotation.Retry; 
import io.github.resilience4j.ratelimiter.annotation.RateLimiter; 
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    // --- LOGIN AVEC RÉSILIENCE ET LIMITATION ---
    @CircuitBreaker(name = "userService", fallbackMethod = "loginFallback")
    //@RateLimiter(name = "loginLimiter", fallbackMethod = "loginRateLimitFallback")
    public TokenDto login(LoginRequest request) {
        Authentication authenticate = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        if (authenticate.isAuthenticated())
            return TokenDto.builder()
                    .token(jwtService.generateToken(request.getUsername()))
                    .build();
        else throw new WrongCredentialsException("Wrong credentials");
    }

    // Fallback pour Circuit Breaker (quand User-Service est en panne)
    public TokenDto loginFallback(LoginRequest request, Exception e) {
        return TokenDto.builder()
                .token("Erreur : Le service de vérification est indisponible. Veuillez réessayer plus tard.")
                .build();
    }

    // Fallback pour Rate Limiter (quand on va trop vite)
    public TokenDto loginRateLimitFallback(LoginRequest request, Exception e) {
        return TokenDto.builder()
                .token("Trop de tentatives de connexion. Réessayez dans 10 secondes.")
                .build();
    }

    // --- REGISTER ---
    public RegisterDto register(RegisterRequest request) {
        try {
            ResponseEntity<RegisterDto> registerDto = userServiceClient.save(request);
            if(registerDto != null){
                return registerDto.getBody();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public RegisterDto registerFallback(RegisterRequest request, Exception e) {
        return RegisterDto.builder()
                .username("Service temporairement indisponible")
                .email("Veuillez réessayer plus tard")
                .build();
    }
}
