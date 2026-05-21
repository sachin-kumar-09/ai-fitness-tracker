package app.fitness.activitytracker.dto;

import app.fitness.activitytracker.entity.ActivityType;

import java.time.LocalDateTime;
import java.util.Map;

public record ActivityRequest (
        String userId,
        ActivityType activityType,
        Integer duration,
        Integer caloriesBurned,
        LocalDateTime startTime,
        Map<String, Object> additionalMetrics
) {}