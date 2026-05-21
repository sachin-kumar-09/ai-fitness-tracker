package app.fitness.aiservice.controller;

import app.fitness.aiservice.entity.Recommendation;
import app.fitness.aiservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/user")
    public ResponseEntity<Object> getUserRecommendation(@RequestParam(required = true) String userId) {
        try{
            log.info("RecommendationController :: getUserRecommendation at {}", System.currentTimeMillis());
            log.info("payload : {}", userId);
            if(userId == null){
                return ResponseEntity.badRequest().body("userId is null");
            }
            return ResponseEntity.ok(recommendationService.getUserRecommendation(userId));
        } catch (RuntimeException e) {
            log.info("No recommendation on this userId: {}", userId);
            return ResponseEntity.badRequest().body("No recommendation for the user with userId: " + userId);
        } finally {
            log.info("Exiting RecommendationController :: getUserRecommendation at {}", System.currentTimeMillis());
        }
    }

    @GetMapping("/activity")
    public ResponseEntity<Object> getActivityRecommendation(@RequestParam(required = true) String activityId) {
        try{
            log.info("RecommendationController :: getActivityRecommendation at {}", System.currentTimeMillis());
            log.info("payload : {}", activityId);
            if(activityId == null){
                return ResponseEntity.badRequest().body("activityId is null");
            }
            return ResponseEntity.ok(recommendationService.getActivityRecommendation(activityId));
        } catch (RuntimeException e) {
            log.info("No recommendation on this activityId: {}", activityId);
            return ResponseEntity.badRequest().body("No recommendation for this activity with acitivityId: " + activityId);
        } finally {
            log.info("Exiting RecommendationController :: getActivityRecommendation at {}", System.currentTimeMillis());
        }
    }
}