package app.fitness.aiservice.service;

import app.fitness.aiservice.entity.Activity;
import app.fitness.aiservice.entity.Recommendation;

public interface ActivityAIService {
    public Recommendation generateRecommendation(Activity activity);
}
