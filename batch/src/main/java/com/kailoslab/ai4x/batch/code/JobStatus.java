package com.kailoslab.ai4x.batch.code;

import com.kailoslab.ai4x.commons.annotation.CodeGroup;
import com.kailoslab.ai4x.commons.annotation.Title;

@CodeGroup
@Title("Job 상태")
public enum JobStatus {
    SCHEDULED,
    RESCHEDULED,
    STARTED,
    PAUSED,
    RESUMED
}
