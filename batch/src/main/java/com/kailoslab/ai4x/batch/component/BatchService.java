package com.kailoslab.ai4x.batch.component;

import com.kailoslab.ai4x.batch.code.BatchCommand;
import com.kailoslab.ai4x.batch.code.JobStatus;
import com.kailoslab.ai4x.batch.data.BatchJobInfoRepository;
import com.kailoslab.ai4x.batch.data.entity.BatchJobInfo;
import com.kailoslab.ai4x.batch.exception.BatchException;
import com.kailoslab.ai4x.batch.exception.InvalidBatchCommandException;
import com.kailoslab.ai4x.batch.exception.InvalidBatchInfoException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@AllArgsConstructor
@Service
public class BatchService {

    private final Scheduler scheduler;
    private final SchedulerFactoryBean schedulerFactoryBean;
    private final BatchJobInfoRepository batchJobInfoRepository;
    private final ApplicationContext applicationContext;
    private final BatchJobCreator batchJobCreator;

    public SchedulerMetaData getMetaData() throws SchedulerException {
        return scheduler.getMetaData();
    }

    public List<BatchJobInfo> getAllJobList() {
        return StreamSupport.stream(batchJobInfoRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public void save(BatchJobInfo jobInfo) throws BatchException {
        start(jobInfo, null);
    }

    public void start(BatchJobInfo jobInfo, Map<String, Object> jobDataMap) throws BatchException {
        if(StringUtils.isAnyEmpty(jobInfo.getJobName(), jobInfo.getJobGroup(), jobInfo.getJobClass())) {
            throw new InvalidBatchInfoException();
        }
        if (!StringUtils.isEmpty(jobInfo.getCronExpression())
                && jobInfo.getCronExpression().length() > 0) {
            jobInfo.setCronJob(true);
        } else {
            jobInfo.setCronJob(false);
            jobInfo.setRepeatTime((long) 1);
        }

        if (StringUtils.isEmpty(jobInfo.getJobId()) || !batchJobInfoRepository.existsById(jobInfo.getJobId())) {
            log.info("Job Info: {}", jobInfo);
            scheduleNewJob(jobInfo, null);
        } else {
            updateScheduleJob(jobInfo);
        }
        log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " created.");
    }

    private void scheduleNewJob(BatchJobInfo jobInfo, Map<String, Object> jobDataMap) throws BatchException {
        if (StringUtils.isEmpty(jobInfo.getJobId())) {
            jobInfo.setJobId(batchJobInfoRepository.generateId(jobInfo.getJobGroup(), jobInfo.getJobName()));
        }

        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobDetail jobDetail = JobBuilder
                    .newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
                    .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup()).build();

            if(jobDataMap != null) {
                jobDetail.getJobDataMap().putAll(jobDataMap);
            }

            if (!scheduler.checkExists(jobDetail.getKey())) {

                jobDetail = batchJobCreator.createJob(
                        (Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()), false, applicationContext,
                        jobInfo.getJobName(), jobInfo.getJobGroup());

                Trigger trigger;
                if (jobInfo.getCronJob()) {
                    trigger = batchJobCreator.createCronTrigger(
                            jobInfo.getJobName(),
                            new Date(),
                            jobInfo.getCronExpression(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                } else {
                    trigger = batchJobCreator.createSimpleTrigger(
                            jobInfo.getJobName(),
                            new Date(),
                            jobInfo.getRepeatTime(),

                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                }
                scheduler.scheduleJob(jobDetail, trigger);
                jobInfo.setJobStatus(JobStatus.SCHEDULED);
                batchJobInfoRepository.save(jobInfo);
                log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " scheduled.");
            } else {
                throw new BatchException("Already exist job %s".formatted(jobInfo.getJobName()));
            }
        } catch (ClassNotFoundException e) {
            throw new BatchException("Class Not Found - %s".formatted(jobInfo.getJobClass()), e);
        } catch (SchedulerException e) {
            throw new BatchException(e.getMessage(), e);
        }
    }

    private void updateScheduleJob(BatchJobInfo jobInfo) {
        Trigger newTrigger;
        if (jobInfo.getCronJob()) {

            newTrigger = batchJobCreator.createCronTrigger(
                    jobInfo.getJobName(),
                    new Date(),
                    jobInfo.getCronExpression(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        } else {

            newTrigger = batchJobCreator.createSimpleTrigger(
                    jobInfo.getJobName(),
                    new Date(),
                    jobInfo.getRepeatTime(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        }
        try {
            schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobInfo.getJobName()), newTrigger);
            jobInfo.setJobStatus(JobStatus.RESCHEDULED);
            batchJobInfoRepository.save(jobInfo);
            log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " updated and scheduled.");
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    public boolean isExist(String jobGroup, String jobName) {
        return batchJobInfoRepository.existsById(batchJobInfoRepository.generateId(jobGroup, jobName));
    }

    public boolean delete(String jobGroup, String jobName) {
        return runJob(jobGroup, jobName, BatchCommand.DELETE);
    }

    public boolean runJob(String jobId, String command) {
        Optional<BatchJobInfo> jobInfoOptional = batchJobInfoRepository.findById(jobId);
        return jobInfoOptional.filter(batchJobInfo -> runJob(batchJobInfo, BatchCommand.valueOf(command))).isPresent();
    }

    public boolean runJob(String jobGroup, String jobName, BatchCommand command) {
        Optional<BatchJobInfo> jobInfoOptional = batchJobInfoRepository.findByJobGroupAndJobName(jobGroup, jobName);
        return jobInfoOptional.filter(batchJobInfo -> runJob(batchJobInfo, command)).isPresent();
    }

    public boolean runJob(BatchJobInfo jobInfo, BatchCommand batchCommand) {
        if(ObjectUtils.isEmpty(Arrays.asList(jobInfo, batchCommand))) {
            return false;
        } else {
            try {
                if(batchCommand == BatchCommand.DELETE) {
                    batchJobInfoRepository.delete(jobInfo);
                } else {
                    switch (batchCommand) {
                        case RUN -> jobInfo.setJobStatus(JobStatus.STARTED);
                        case PAUSE -> jobInfo.setJobStatus(JobStatus.PAUSED);
                        case RESUME -> jobInfo.setJobStatus(JobStatus.RESUMED);
                        default -> throw new InvalidBatchCommandException();
                    }

                    batchJobInfoRepository.save(jobInfo);
                }

                JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
                switch (batchCommand) {
                    case RUN -> schedulerFactoryBean.getScheduler().triggerJob(jobKey);
                    case PAUSE -> schedulerFactoryBean.getScheduler().pauseJob(jobKey);
                    case RESUME -> schedulerFactoryBean.getScheduler().resumeJob(jobKey);
                    case DELETE -> {
                        if(!schedulerFactoryBean.getScheduler().deleteJob(jobKey)) {
                            throw new BatchException();
                        }
                    }
                    default -> throw new BatchException();
                }
                log.info("The command({}) for the job({}) was executed successfully.", batchCommand, jobInfo.getJobId());
                return true;
            } catch (Exception e) {
                log.error("Failed to {} the job({})", batchCommand, jobInfo.getJobId(), e);
                return false;
            }
        }
    }
}
