package app.fitness.userservice.services.impl;

import app.fitness.userservice.dto.RegisterRequest;
import app.fitness.userservice.dto.UserResponse;
import app.fitness.userservice.entity.User;
import app.fitness.userservice.repository.UserRepository;
import app.fitness.userservice.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse register(RegisterRequest request) throws Exception {
        log.info("Entering inside UserService : register at {}", request);
        log.info("Payload: {}", request);
        if(userRepository.existsByEmail(request.email())) {
            User existUser = userRepository.findByEmail(request.email());
            UserResponse response = new UserResponse();
            response.setEmail(existUser.getEmail());
            response.setPassword(existUser.getPassword());
            response.setLastName(existUser.getLastName());
            response.setFirstName(existUser.getFirstName());
            response.setId(existUser.getId());
            response.setKeycloakId(existUser.getKeycloakId());
            response.setCreatedAt(existUser.getCreatedAt());
            return response;
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setKeycloakId(request.keycloakId());

        User savedUser = userRepository.save(user);
        UserResponse response = new UserResponse();
        response.setEmail(savedUser.getEmail());
        response.setPassword(savedUser.getPassword());
        response.setLastName(savedUser.getLastName());
        response.setFirstName(savedUser.getFirstName());
        response.setId(savedUser.getId());
        response.setKeycloakId(savedUser.getKeycloakId());
        response.setCreatedAt(savedUser.getCreatedAt());
        return response;
    }

    @Override
    public UserResponse getUserProfile(String email) throws Exception {
        log.info("Entering UserService : getUserProfile at {}", email);
        log.info("Payload: {}", email);
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new Exception("User not found");
        }

        UserResponse response = new UserResponse();
        response.setEmail(user.getEmail());
        response.setPassword(user.getPassword());
        response.setLastName(user.getLastName());
        response.setFirstName(user.getFirstName());
        response.setId(user.getId());
        response.setKeycloakId(user.getKeycloakId());
        response.setCreatedAt(user.getCreatedAt());

        return response;
    }

    @Override
    public Boolean existsByUserId(String keycloakId) {
        log.info("Entering UserService : existsByUserId at {}", keycloakId);
        log.info("Payload: {}", keycloakId);
        return userRepository.existsByKeycloakId(keycloakId);
    }
}