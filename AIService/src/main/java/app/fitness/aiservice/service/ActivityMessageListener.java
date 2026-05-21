package app.fitness.aiservice.service;


import app.fitness.aiservice.entity.Activity;

public interface ActivityMessageListener {
    public void processActivity(Activity activity);
}
