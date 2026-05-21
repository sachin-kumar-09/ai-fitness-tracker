package app.fitness.aiservice.controller;

import app.fitness.aiservice.entity.Recommendation;
import app.fitness.aiservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getUserRecommendation(@PathVariable String userId) {
        try{
            log.info("RecommendationController :: getUserRecommendation at {}", System.currentTimeMillis());
            log.info("payload : {}", userId);
            return ResponseEntity.ok(recommendationService.getUserRecommendation(userId));
        } catch (RuntimeException e) {
            log.info("No recommendation on this userId: {}", userId);
            return ResponseEntity.badRequest().body("No recommendation for the user with userId: " + userId);
        } finally {
            log.info("Exiting RecommendationController :: getUserRecommendation at {}", System.currentTimeMillis());
        }
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Object> getActivityRecommendation(@PathVariable String activityId) {
        try{
            log.info("RecommendationController :: getActivityRecommendation at {}", System.currentTimeMillis());
            log.info("payload : {}", activityId);
            return ResponseEntity.ok(recommendationService.getActivityRecommendation(activityId));
        } catch (RuntimeException e) {
            log.info("No recommendation on this activityId: {}", activityId);
            return ResponseEntity.badRequest().body("No recommendation for this activity with acitivityId: " + activityId);
        } finally {
            log.info("Exiting RecommendationController :: getActivityRecommendation at {}", System.currentTimeMillis());
        }
    }
}