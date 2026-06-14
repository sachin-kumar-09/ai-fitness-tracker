package app.fitness.gateway.user.service.impl;

import app.fitness.gateway.user.dto.RegisterRequest;
import app.fitness.gateway.user.dto.UserResponse;
import app.fitness.gateway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    public final WebClient userServiceWebClient;

    @Override
    public Mono<Boolean> validateUser(String keyCloakId) {
        log.info("Entering Gateway :: UserServiceImpl :: validateUser at {}", System.currentTimeMillis());
        try {
            return userServiceWebClient.get()
                    .uri("/api/users/{keycloakId}/validate", keyCloakId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(WebClientResponseException.class, e -> {
                        log.error("Downstream API failed for Keycloak ID: {}. Status: {}, Body: {}",
                                keyCloakId, e.getStatusCode(), e.getResponseBodyAsString());
                        if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.error(new RuntimeException("User not found : " + keyCloakId));
                        } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                            return Mono.error(new RuntimeException("Invalid user id : " + keyCloakId));
                        }
                        return Mono.error(new RuntimeException("Unexpected Error : " + keyCloakId));
                    });
        } catch (WebClientResponseException e) {
            log.error("Error occurred while validating user: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while validating user: {}", e.getMessage());
        }
        return Mono.just(false);
    }

    @Override
    public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {
        log.info("Entering Gateway :: UserServiceImpl :: registerUser at {}", System.currentTimeMillis());
        log.info("Registering User: {}", registerRequest);
        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if(e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new RuntimeException("Invalid registration details: " + e.getMessage()));
                    }
                    return Mono.error(new RuntimeException("Unexpected Error during registration: " + e.getMessage()));
                })
                .doOnError(e -> log.error("Error occurred while registering user: {}", e.getMessage()));
    }
}
