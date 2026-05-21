package app.fitness.aiservice.service.impl;

import app.fitness.aiservice.entity.Activity;
import app.fitness.aiservice.entity.Recommendation;
import app.fitness.aiservice.repository.RecommendationRepository;
import app.fitness.aiservice.service.ActivityAIService;
import app.fitness.aiservice.service.GeminiService;
import app.fitness.aiservice.service.impl.helper.ActivityAIServiceHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ActivityAIServiceImpl implements ActivityAIService {
    private final GeminiService geminiService;
    private final RecommendationRepository repository;


    @Override
    public Recommendation generateRecommendation(Activity activity) {
        log.info("Entering ActivityAIService :: generateRecommendation at {}", System.currentTimeMillis());
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getRecommendations(prompt);

        Recommendation recommendation = ActivityAIServiceHelper.processRecommendation(activity, aiResponse);

        try{
            return repository.save(recommendation);
        } catch (Exception e) {
            throw new RuntimeException("Unable to save recommendation at " + System.currentTimeMillis(), e);
        }
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
        
                        You are an expert AI fitness coach.
                                Analyze the workout activity below and generate personalized fitness recommendations.
                        
                                Activity Details:
                                - Activity Type: %s
                                - Duration: %d minutes
                                - Calories Burned: %d
                                - Start Time: %s
                                - Additional Metrics: %s
                        
                                Return ONLY valid JSON.
                                Do not include markdown, explanation, notes, or code block formatting.
                        
                                JSON Format:
                                {
                                  "analysis" : {
                                    "activityType": "string",
                                    "performanceSummary": "string",
                                    "caloriesBurned": "string",
                                    "healthInsights": "string",
                                    "intensityLevel": "string",
                                    "recoveryAdvice": {
                                      "hydration": "string",
                                      "stretching": "string",
                                      "rest": "string"
                                    },
                                    "nextWorkoutRecommendation": {
                                      "recommendedActivity": "string",
                                      "recommendedDuration": "string",
                                      "reason": "string"
                                    },
                                    "improvementSuggestions": [
                                        "area" : "recommendation": "string"
                                  ],
                                  }
                                }
                        
                                Rules:
                                - Return strictly valid JSON.
                                - No extra text outside JSON.
                                - Keep recommendations personalized and concise.
                                - Base recommendation only on provided activity data.
                                - improvementSuggestions must be an array.
                                - generatedAt should be current timestamp.
                        
                                Provide detailed analysis focusing on performance, improvements, next workout suggestions and safety guidelines.
                                Ensure the response follow the EXACT JSON format shown above.

        """,
                activity.getActivityType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getStartTime(),
                activity.getAdditionalMetrics()
        );
    }
}
