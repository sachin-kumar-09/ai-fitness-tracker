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
            throw new Exception("User already exists.");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        User savedUser = userRepository.save(user);
        UserResponse response = new UserResponse();
        response.setEmail(savedUser.getEmail());
        response.setPassword(savedUser.getPassword());
        response.setLastName(user.getLastName());
        response.setFirstName(user.getFirstName());
        response.setId(savedUser.getId());
        response.setCreatedAt(savedUser.getCreatedAt());
        return response;
    }

    @Override
    public UserResponse getUserProfile(String email) throws Exception {
        log.info("Entering UserService : getUserProfile at {}", email);
        log.info("Payload: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User does not exist."));
        UserResponse response = new UserResponse();
        response.setEmail(user.getEmail());
        response.setPassword(user.getPassword());
        response.setLastName(user.getLastName());
        response.setFirstName(user.getFirstName());
        response.setId(user.getId());
        response.setCreatedAt(user.getCreatedAt());

        return response;
    }

    @Override
    public Boolean existsByUserId(String userId) {
        log.info("Entering UserService : existsByUserId at {}", userId);
        log.info("Payload: {}", userId);
        return userRepository.existsById(userId);
    }
}