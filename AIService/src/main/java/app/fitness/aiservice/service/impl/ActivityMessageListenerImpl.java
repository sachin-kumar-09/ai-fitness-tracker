package app.fitness.aiservice.service.impl;

import app.fitness.aiservice.entity.Activity;
import app.fitness.aiservice.service.ActivityAIService;
import app.fitness.aiservice.service.ActivityMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListenerImpl implements ActivityMessageListener {

    private final ActivityAIService activityAIService;

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "activity-processor-group")
    @Override
    public void processActivity(Activity activity) {
        log.info("Entering ActivityMessageListener :: processActivity at {}", System.currentTimeMillis());
        log.info("Received Activity for processing: {}", activity);

        activityAIService.generateRecommendation(activity);
    }

}
