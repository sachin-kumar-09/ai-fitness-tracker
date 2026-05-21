package app.fitness.aiservice.service.impl.helper;

import app.fitness.aiservice.commons.AIServiceConstants;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityAIServiceHelper {

    private void ActivityAIServiceHelper(){}

    public static void addAnalysisSection(
            StringBuilder fullAnalysis,
            JsonNode analysisNode,
            String key,
            String prefix) {

        if (analysisNode.has(key)) {
            fullAnalysis.append(prefix)
                    .append(
                            analysisNode.path(key)
                                    .asText()
                    )
                    .append("\n");
        }
    }

    public static List<String> extractImprovements(
            JsonNode improvementSuggestions) {

        List<String> improvements =
                new ArrayList<>();

        if (improvementSuggestions.isArray()) {

            improvementSuggestions.forEach(
                    improvement -> {

                        String area =
                                improvement.path(
                                        AIServiceConstants.AREA
                                ).asText();

                        String recommendation =
                                improvement.path(
                                        AIServiceConstants.RECOMMENDATION
                                ).asText();

                        improvements.add(
                                String.format(
                                        "%s (%s)",
                                        area,
                                        recommendation
                                )
                        );
                    });
        }

        return improvements.isEmpty() ? Collections.singletonList("No specific improvement provided") : improvements;
    }

    public static List<String> extractSafety(
            JsonNode recoveryNode) {

        List<String> safety =
                new ArrayList<>();

        if (recoveryNode.has(
                AIServiceConstants.HYDRATION)) {

            safety.add(
                    AIServiceConstants.LABEL_HYDRATION
                            + recoveryNode.path(
                            AIServiceConstants.HYDRATION
                    ).asText()
            );
        }

        if (recoveryNode.has(
                AIServiceConstants.STRETCHING)) {

            safety.add(
                    AIServiceConstants.LABEL_STRETCHING
                            + recoveryNode.path(
                            AIServiceConstants.STRETCHING
                    ).asText()
            );
        }

        if (recoveryNode.has(
                AIServiceConstants.REST)) {

            safety.add(
                    AIServiceConstants.LABEL_REST
                            + recoveryNode.path(
                            AIServiceConstants.REST
                    ).asText()
            );
        }

        return safety;
    }

    public static List<String> extractSuggestions(
            JsonNode workoutNode) {

        List<String> suggestions =
                new ArrayList<>();

        if (workoutNode.has(
                AIServiceConstants.RECOMMENDED_ACTIVITY)) {

            suggestions.add(
                    AIServiceConstants
                            .LABEL_RECOMMENDED_ACTIVITY
                            + workoutNode.path(
                            AIServiceConstants
                                    .RECOMMENDED_ACTIVITY
                    ).asText()
            );
        }

        if (workoutNode.has(
                AIServiceConstants.RECOMMENDED_DURATION)) {

            suggestions.add(
                    AIServiceConstants
                            .LABEL_RECOMMENDED_DURATION
                            + workoutNode.path(
                            AIServiceConstants
                                    .RECOMMENDED_DURATION
                    ).asText()
            );
        }

        if (workoutNode.has(
                AIServiceConstants.REASON)) {

            suggestions.add(
                    AIServiceConstants.LABEL_REASON
                            + workoutNode.path(
                            AIServiceConstants.REASON
                    ).asText()
            );
        }

        return suggestions;
    }
}
