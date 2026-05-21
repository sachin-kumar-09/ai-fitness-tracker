package app.fitness.activitytracker.service.impl;

import app.fitness.activitytracker.dto.AcitivityResponse;
import app.fitness.activitytracker.dto.ActivityRequest;
import app.fitness.activitytracker.entity.Activity;
import app.fitness.activitytracker.repository.ActivityRepository;
import app.fitness.activitytracker.service.ActivityService;
import app.fitness.activitytracker.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final KafkaTemplate<String, Activity> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;

    @Override
    public AcitivityResponse trackActivity(ActivityRequest request) {
        log.info("Inside ActivityService :: trackActivity at {}", System.currentTimeMillis());
        boolean isValidUser = userValidationService.validateUser(request.userId());

        if(!isValidUser) {
            throw new RuntimeException("Invalid User");
        }

        Activity activity = Activity.builder()
                .userId(request.userId())
                .activityType(request.activityType())
                .duration(request.duration())
                .caloriesBurned(request.caloriesBurned())
                .startTime(request.startTime())
                .additionalMetrics(request.additionalMetrics())
                .build();
        Activity savedActivity = activityRepository.save(activity);

        AcitivityResponse activityResponse = mapToResponse(savedActivity);

        try {
            log.info("Sending topic from Kafka {}", topicName);
            kafkaTemplate.send(topicName, savedActivity.getUserId(), savedActivity);
        } catch (Exception e) {
            log.info("Sending topics from kafka failed at {}", System.currentTimeMillis());
        }
        log.info("Exiting ActivityService :: trackActivity :: activityResponse: {}", activityResponse);
        return activityResponse;
    }

    private AcitivityResponse mapToResponse(Activity activity) {
        log.info("Inside ActivityService :: mapToResponse :: activity: {}", activity);
        return AcitivityResponse.builder()
                .id(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getActivityType())
                .duration(activity.getDuration())
                .caloriesBurned(activity.getCaloriesBurned())
                .startTime(activity.getStartTime())
                .additionalMetrics(activity.getAdditionalMetrics())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .build();
    }
}
