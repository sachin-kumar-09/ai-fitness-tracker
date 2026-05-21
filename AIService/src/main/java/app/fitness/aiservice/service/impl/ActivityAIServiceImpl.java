package app.fitness.aiservice.service.impl;

import app.fitness.aiservice.commons.AIServiceConstants;
import app.fitness.aiservice.entity.Activity;
import app.fitness.aiservice.entity.Recommendation;
import app.fitness.aiservice.repository.RecommendationRepository;
import app.fitness.aiservice.service.ActivityAIService;
import app.fitness.aiservice.service.GeminiService;
import app.fitness.aiservice.service.impl.helper.ActivityAIServiceHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

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
        String aiResponse =
                geminiService.getRecommendations(prompt);

        Recommendation recommendation =
                Recommendation.builder()
                        .activityId(activity.getId())
                        .userId(activity.getUserId())
                        .recommendation(aiResponse)
                        .build();

        try{
            log.info("ActivityAIService :: saving recommendation at {}", System.currentTimeMillis());
            Recommendation savedRecommendation = repository.save(recommendation);
            log.info("ActivityAIService :: saved recommendation at {}", System.currentTimeMillis());
        } catch (Exception e) {
            log.error("ActivityAIService :: failed at {}", System.currentTimeMillis(), e);
            throw new RuntimeException("Unable to save recommendation at " + System.currentTimeMillis(), e);
        }

        return processRecommendation(activity, aiResponse);
    }

    private Recommendation processRecommendation(Activity activity, String aiResponse) {
        log.info("ActivityAIService :: processing recommendation at {}", System.currentTimeMillis());
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);
            JsonNode textNode = rootNode.path(AIServiceConstants.CANDIDATES)
                    .get(0)
                    .path(AIServiceConstants.CONTENT)
                    .get(AIServiceConstants.PARTS)
                    .get(0)
                    .path(AIServiceConstants.TEXT);

            String jsonContent = textNode.asText()
                    .replaceAll("\\n", "")
                    .trim();

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path(AIServiceConstants.ANALYSIS);

            StringBuilder fullAnalysis = new StringBuilder();

            ActivityAIServiceHelper.addAnalysisSection(fullAnalysis, analysisNode, AIServiceConstants.ACTIVITY_TYPE, AIServiceConstants.LABEL_ACTIVITY_TYPE);
            ActivityAIServiceHelper.addAnalysisSection(fullAnalysis, analysisNode, AIServiceConstants.PERFORMANCE_SUMMARY, AIServiceConstants.LABEL_SUMMARY);
            ActivityAIServiceHelper.addAnalysisSection(fullAnalysis, analysisNode, AIServiceConstants.CALORIES_BURNED, AIServiceConstants.LABEL_CALORIES);
            ActivityAIServiceHelper.addAnalysisSection(fullAnalysis, analysisNode, AIServiceConstants.HEALTH_INSIGHTS, AIServiceConstants.LABEL_HEALTH);

            JsonNode recoveryNode = analysisNode.path(AIServiceConstants.RECOVERY_ADVICE);
            ActivityAIServiceHelper.addAnalysisSection(fullAnalysis, recoveryNode, AIServiceConstants.HYDRATION, AIServiceConstants.LABEL_HYDRATION);
            ActivityAIServiceHelper.addAnalysisSection(fullAnalysis, recoveryNode, AIServiceConstants.STRETCHING, AIServiceConstants.LABEL_STRETCHING);
            ActivityAIServiceHelper.addAnalysisSection(fullAnalysis, recoveryNode, AIServiceConstants.REST, AIServiceConstants.LABEL_REST);

            List<String> improvements = ActivityAIServiceHelper.extractImprovements(analysisJson.path(AIServiceConstants.IMPROVEMENT_SUGGESTIONS));

            JsonNode nextWorkoutRecommendation = analysisNode.path(AIServiceConstants.NEXT_WORKOUT_RECOMMENDATION);
            ActivityAIServiceHelper.addAnalysisSection(fullAnalysis, nextWorkoutRecommendation, AIServiceConstants.RECOMMENDED_ACTIVITY, AIServiceConstants.LABEL_RECOMMENDED_ACTIVITY);
            ActivityAIServiceHelper.addAnalysisSection(fullAnalysis, nextWorkoutRecommendation, AIServiceConstants.RECOMMENDED_DURATION, AIServiceConstants.LABEL_RECOMMENDED_DURATION);
            ActivityAIServiceHelper.addAnalysisSection(fullAnalysis, nextWorkoutRecommendation, AIServiceConstants.REASON, AIServiceConstants.LABEL_REASON);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
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
                                  "activityType": "string",
                                  "performanceSummary": "string",
                                  "healthInsights": "string",
                                  "improvementSuggestions": [
                                    "string"
                                  ],
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
                                  "intensityLevel": "LOW | MODERATE | HIGH",
                                  "generatedAt": "ISO_DATE_TIME"
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
