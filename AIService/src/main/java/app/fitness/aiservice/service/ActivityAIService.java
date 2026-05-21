package app.fitness.aiservice.service;

import app.fitness.aiservice.entity.Activity;

public interface ActivityAIService {
    public void generateRecommendation(Activity activity);
}
