package com.kailoslab.ai4x.collector;

import lombok.AllArgsConstructor;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@AllArgsConstructor
public class CollectorJob extends QuartzJobBean {
    public static final String KEY_COLLECTOR_NAME = "collectorName";
    public static final String KEY_COLLECTOR_PROPERTIES = "collectorProperties";

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        CollectorWrapper collectorWrapper = (CollectorWrapper) context.getJobDetail().getJobDataMap().get(KEY_COLLECTOR_NAME);
        Object[] properties = (Object[]) context.getJobDetail().getJobDataMap().get(KEY_COLLECTOR_PROPERTIES);
        if(collectorWrapper != null) {
            try {
                collectorWrapper.execute(properties);
            } catch (CollectorException e) {
                throw new JobExecutionException(String.format("Cannot execute a collector [%s].", collectorWrapper.getName()), e);
            }
        } else {
            throw new JobExecutionException(String.format("Cannot find a collector [%s].", collectorWrapper.getName()));
        }
    }
}
