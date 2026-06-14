package app.fitness.gateway.user.service;

import app.fitness.gateway.user.dto.RegisterRequest;
import app.fitness.gateway.user.dto.UserResponse;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<Boolean> validateUser(String userId);
    Mono<UserResponse> registerUser(RegisterRequest registerRequest);
}
