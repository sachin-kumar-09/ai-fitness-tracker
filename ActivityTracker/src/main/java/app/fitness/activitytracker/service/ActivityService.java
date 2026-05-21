package app.fitness.activitytracker.service;

import app.fitness.activitytracker.dto.AcitivityResponse;
import app.fitness.activitytracker.dto.ActivityRequest;
import app.fitness.activitytracker.entity.ActivityType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

public interface ActivityService {
    AcitivityResponse trackActivity(ActivityRequest request);
}
