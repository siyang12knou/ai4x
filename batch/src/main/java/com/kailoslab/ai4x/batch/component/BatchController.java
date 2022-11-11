package com.kailoslab.ai4x.batch.component;

import com.kailoslab.ai4x.commons.data.dto.ResultMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/batch")
public class BatchController {

    private final BatchService scheduleService;

    @GetMapping("/metaData")
    public Object metaData() throws SchedulerException {
        return scheduleService.getMetaData();
    }

    @GetMapping("/getAllJobs")
    public Object getAllJobs() {
        return scheduleService.getAllJobList();
    }

    @PostMapping(value = "/{jobId}/{command}")
    public Object runJob(@PathVariable String jobId, @PathVariable String command) {
        ResultMessageDto message;
        try {
            if(scheduleService.runJob(jobId, command)) {
                message = new ResultMessageDto();
            } else {
                message = new ResultMessageDto(false, "Cannot %s %s".formatted(command, jobId));
            }
        } catch (Exception e) {
            message = new ResultMessageDto(false, "Cannot %s %s: %s".formatted(command, jobId, e.getMessage()));
        }
        return message;
    }
}