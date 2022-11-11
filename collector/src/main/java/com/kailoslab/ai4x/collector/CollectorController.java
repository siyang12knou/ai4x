package com.kailoslab.ai4x.collector;

import com.kailoslab.ai4x.commons.data.dto.ResultMessageDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/collector")
public class CollectorController {

    private final CollectorService collectorManager;

    @PutMapping("/{name}/properties")
    public ResultMessageDto saveProperties(@PathVariable String name, @RequestBody(required = false) Map<String, Object> properties) {
        try {
            collectorManager.saveProperties(name, properties);
            return new ResultMessageDto();
        } catch (CollectorException e) {
            return new ResultMessageDto(false, e.getMessage());
        }
    }

    @PutMapping("/{name}/start")
    public ResultMessageDto startCollector(@PathVariable String name) {
        try {
            collectorManager.start(name);
            return new ResultMessageDto();
        } catch (CollectorException e) {
            return new ResultMessageDto(false, e.getMessage());
        }
    }

    @PutMapping("/{name}/stop")
    public ResultMessageDto stopCollector(@PathVariable String name) {
        try {
            collectorManager.stop(name);
            return new ResultMessageDto();
        } catch (CollectorException e) {
            return new ResultMessageDto(false, e.getMessage());
        }
    }
}
