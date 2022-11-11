package com.kailoslab.ai4x.batch.code;

import com.kailoslab.ai4x.commons.annotation.CodeGroup;
import com.kailoslab.ai4x.commons.annotation.Title;

@CodeGroup
@Title("배치 Job 제어 명령어")
public enum BatchCommand {
    RUN,
    PAUSE,
    RESUME,
    DELETE
}
