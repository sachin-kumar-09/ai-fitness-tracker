package app.fitness.aiservice.service.impl;

import app.fitness.aiservice.entity.Recommendation;
import app.fitness.aiservice.repository.RecommendationRepository;
import app.fitness.aiservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository repository;

    @Override
    public List<Recommendation> getUserRecommendation(String userId) {
        log.info("RecommendationService :: getUserRecommendations at {}", System.currentTimeMillis());
        log.info("payload : {}", userId);
        List<Recommendation> recommendations = repository.findByUserId(userId);

        if(recommendations.isEmpty() || recommendations.size() == 0) {
            log.info("User has not any activity record so no recommendations can't be generated.");
            throw new RuntimeException("Recommendations can not be generated.");
        }

        return recommendations;
    }

    @Override
    public Recommendation getActivityRecommendation(String activityId) {
        log.info("RecommendationService :: getActivityRecommendation at {}", System.currentTimeMillis());
        log.info("payload : {}", activityId);
        return repository.findByActivityId(activityId)
                .orElseThrow(() -> new RuntimeException("No Recommendation found for this activity."));
    }
}
