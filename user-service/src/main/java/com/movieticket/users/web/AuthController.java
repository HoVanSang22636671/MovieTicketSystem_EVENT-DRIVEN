package com.movieticket.users.web;

import com.movieticket.contracts.RabbitConstants;
import com.movieticket.contracts.UserRegisteredEvent;
import com.movieticket.users.domain.User;
import com.movieticket.users.repo.UserRepository;
import com.movieticket.users.web.dto.LoginRequest;
import com.movieticket.users.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Map;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    public AuthController(UserRepository userRepository, RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "USERNAME_EXISTS"));
        }

        User user = new User(request.getUsername(), request.getPassword());
        user = userRepository.save(user);

        UserRegisteredEvent event = new UserRegisteredEvent(user.getId(), user.getUsername(), Instant.now());
        rabbitTemplate.convertAndSend(RabbitConstants.EXCHANGE_MOVIE_TICKET, RabbitConstants.RK_USER_REGISTERED, event);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", user.getId(), "username", user.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .filter(u -> u.getPassword().equals(request.getPassword()))
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(Map.of("id", u.getId(), "username", u.getUsername())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "INVALID_CREDENTIALS")));
    }
}
