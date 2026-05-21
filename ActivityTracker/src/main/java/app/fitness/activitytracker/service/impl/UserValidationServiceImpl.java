package app.fitness.activitytracker.service.impl;

import app.fitness.activitytracker.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationServiceImpl implements UserValidationService {

    public final WebClient userServiceWebClient;

    @Override
    public boolean validateUser(String userId) {
        log.info("Inside ActivityService :: validateUser :: userId: {}", userId);
        try {
            boolean isValidUser = userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            log.info("userId is valid: {}", isValidUser);
            return isValidUser;
        } catch (WebClientResponseException e) {
            log.error("Error occurred while validating user: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error occurred while validating user: {}", e.getMessage());
            return false;
        }
    }
}
