package ScheduleJob;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TFIDFJob implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        TFIDFTool.calTFIDF();
    }
}