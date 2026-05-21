package app.fitness.activitytracker.repository;

import app.fitness.activitytracker.entity.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {
}
