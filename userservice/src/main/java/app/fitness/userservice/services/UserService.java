package app.fitness.userservice.services;

import app.fitness.userservice.dto.RegisterRequest;
import app.fitness.userservice.dto.UserResponse;

public interface UserService {
    UserResponse register(RegisterRequest request) throws Exception;

    UserResponse getUserProfile(String email) throws Exception;

    Boolean existsByUserId(String keycloakId);
}
