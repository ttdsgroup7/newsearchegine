package ScheduleJob;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class CronScheduler {
    public static void main(String[] args) throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(TFIDFJob.class)
                .withIdentity("TFIDFJob").build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("cronTrigger")
                //cron表达式 这里定义的是  在每天中午12点执行一次
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 * * ?"))
                .build();
        SchedulerFactory factory = new StdSchedulerFactory();
        //创建调度器
        Scheduler scheduler = factory.getScheduler();
        //启动调度器
        scheduler.start();
        //jobDetail和trigger加入调度
        scheduler.scheduleJob(jobDetail, trigger);
    }
}