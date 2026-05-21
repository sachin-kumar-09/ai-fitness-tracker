package app.fitness.activitytracker.controller;

import app.fitness.activitytracker.dto.AcitivityResponse;
import app.fitness.activitytracker.dto.ActivityRequest;
import app.fitness.activitytracker.service.ActivityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
@Slf4j
public class ActivityController {
    private ActivityService activityService;

    @PostMapping
    public ResponseEntity<AcitivityResponse> trackActivity(@RequestBody ActivityRequest request) {
        log.info("Inside ActivityController :: trackActivity at {}", System.currentTimeMillis());
        log.info("Request Payload:  {}", request);
        try{
            return ResponseEntity.ok(activityService.trackActivity(request));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            log.info("Exiting ActivityController :: trackActivityController Method");
        }
    }
}
