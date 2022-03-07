package top.caohongchuan.newsrecommand.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.caohongchuan.newsrecommand.service.JobService;

@Component
public class ScheduledTask{

    @Autowired
    JobService jobService;

    @Scheduled(cron = "0 0 4 * * ?")
    public void scheduleFixedDelayTask() {
        jobService.JobSetter(true, true, true);
        jobService.deleteExpiryRecommendation();
        jobService.executeInstantJobForAllUsers();
    }
}
