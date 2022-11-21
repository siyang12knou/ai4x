package com.kailoslab.ai4x.batch.data.entity;

import com.kailoslab.ai4x.batch.code.JobStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_batch_job_info")
public class BatchJobInfo {

    @Id
    private String jobId;
    private String jobGroup;
    private String jobName;
    private JobStatus jobStatus;
    private String jobClass;
    private String cronExpression;
    private String desc;
    private Long repeatTime = 1L;
    private Boolean cronJob = false;

    public BatchJobInfo(String jobName, String jobGroup, String jobClass) {
        this(jobGroup, jobName, jobClass, null);
    }

    public BatchJobInfo(String jobGroup, String jobName, String jobClass, String desc) {
        this.jobGroup = jobGroup;
        this.jobName = jobName;
        this.jobClass = jobClass;
        this.desc = desc;
    }
}
