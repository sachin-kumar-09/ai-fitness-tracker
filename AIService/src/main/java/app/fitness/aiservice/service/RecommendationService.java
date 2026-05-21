package app.fitness.aiservice.service;

import app.fitness.aiservice.entity.Recommendation;

import java.util.List;

public interface RecommendationService {
    List<Recommendation> getUserRecommendation(String userId);

    Recommendation getActivityRecommendation(String activityId);
}
