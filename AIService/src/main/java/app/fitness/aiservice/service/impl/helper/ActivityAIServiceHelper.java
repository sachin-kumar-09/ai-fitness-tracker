package app.fitness.aiservice.service.impl.helper;

import app.fitness.aiservice.commons.AIServiceConstants;
import app.fitness.aiservice.entity.Activity;
import app.fitness.aiservice.entity.Recommendation;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ActivityAIServiceHelper {

    public static void addAnalysisSection (StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        log.info("ActivityAIServiceHelper :: Entering addAnalysisSection at {}", System.currentTimeMillis());
        if (analysisNode.has(key)) {
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n");
        }
    }

    public static List<String> extractImprovements (JsonNode improvementSuggestions) {
    log.info("ActivityAIServiceHelper :: Entering extractImprovements at {}", System.currentTimeMillis());
        List<String> improvements = new ArrayList<>();

        if (improvementSuggestions.isArray()) {
            improvementSuggestions.forEach(improvement -> {
                        String area = improvement.path(AIServiceConstants.AREA).asText();
                        String recommendation = improvement.path(AIServiceConstants.RECOMMENDATION).asText();
                        improvements.add(String.format("%s (%s)",area,recommendation));
                    }
            );
        }

        return improvements.isEmpty() ? Collections.singletonList("No specific improvement provided") : improvements;
    }

    public static List<String> extractSafety(JsonNode recoveryNode) {
        log.info("ActivityAIServiceHelper :: Entering extractSafety at {}", System.currentTimeMillis());
        List<String> safety = new ArrayList<>();

        if (recoveryNode.has(AIServiceConstants.HYDRATION)) {
            safety.add(AIServiceConstants.LABEL_HYDRATION + recoveryNode.path(AIServiceConstants.HYDRATION).asText());
        }

        if (recoveryNode.has(AIServiceConstants.STRETCHING)) {
            safety.add(AIServiceConstants.LABEL_STRETCHING + recoveryNode.path(AIServiceConstants.STRETCHING).asText());
        }

        if (recoveryNode.has(AIServiceConstants.REST)) {
            safety.add(AIServiceConstants.LABEL_REST + recoveryNode.path(AIServiceConstants.REST).asText());
        }

        return safety;
    }

    public static List<String> extractSuggestions(JsonNode workoutNode) {
        log.info("ActivityAIServiceHelper :: Entering extractSuggestions at {}", System.currentTimeMillis());
        List<String> suggestions = new ArrayList<>();

        if (workoutNode.has(AIServiceConstants.RECOMMENDED_ACTIVITY)) {
            suggestions.add(AIServiceConstants.LABEL_RECOMMENDED_ACTIVITY + workoutNode.path(AIServiceConstants.RECOMMENDED_ACTIVITY).asText());
        }

        if (workoutNode.has(AIServiceConstants.RECOMMENDED_DURATION)) {
            suggestions.add(AIServiceConstants.LABEL_RECOMMENDED_DURATION + workoutNode.path(AIServiceConstants.RECOMMENDED_DURATION).asText());
        }

        if (workoutNode.has(AIServiceConstants.REASON)) {
            suggestions.add(AIServiceConstants.LABEL_REASON+ workoutNode.path(AIServiceConstants.REASON).asText());
        }

        return suggestions;
    }

    public static Recommendation processRecommendation(Activity activity, String aiResponse) {
        log.info("ActivityAIServiceHelper :: processing recommendation at {}", System.currentTimeMillis());
        Recommendation recommendation = null;
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
            ActivityAIServiceHelper.addAnalysisSection(fullAnalysis, analysisNode, AIServiceConstants.INTENSITY_LEVEL, AIServiceConstants.LABEL_INTENSITY_LEVEL);

            JsonNode recoveryNode = analysisNode.path(AIServiceConstants.RECOVERY_ADVICE);
            ActivityAIServiceHelper.addAnalysisSection(fullAnalysis, recoveryNode, AIServiceConstants.HYDRATION, AIServiceConstants.LABEL_HYDRATION);
            ActivityAIServiceHelper.addAnalysisSection(fullAnalysis, recoveryNode, AIServiceConstants.STRETCHING, AIServiceConstants.LABEL_STRETCHING);
            ActivityAIServiceHelper.addAnalysisSection(fullAnalysis, recoveryNode, AIServiceConstants.REST, AIServiceConstants.LABEL_REST);
            List<String> improvements = ActivityAIServiceHelper.extractImprovements(analysisNode.path(AIServiceConstants.IMPROVEMENT_SUGGESTIONS));
            List<String> suggestions = ActivityAIServiceHelper.extractSuggestions(analysisNode.path(AIServiceConstants.NEXT_WORKOUT_RECOMMENDATION));
            List<String> safety = ActivityAIServiceHelper.extractSafety(analysisNode.path(AIServiceConstants.RECOVERY_ADVICE));

            recommendation = Recommendation.builder()
                    .userId(activity.getUserId())
                    .activityId(activity.getId())
                    .recommendation(fullAnalysis.toString())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .build();

        } catch (Exception e) {
            log.error("Failed to process recommendation at {}", System.currentTimeMillis(), e);
            recommendation = createDefaultRecommendation(activity);
        }

        return  recommendation;

    }

    private static Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .userId(activity.getUserId())
                .activityId(activity.getId())
                .recommendation("Unable to generate recommendation.")
                .improvements(Collections.singletonList("Continue with your current routine."))
                .suggestions(Collections.singletonList("Consider consulting a fitness coach."))
                .safety(Arrays.asList(
                        "Always warm up before exercise.",
                        "Stay hydrated.",
                        "Listen to your body."
                ))
                .build();
    }
}
