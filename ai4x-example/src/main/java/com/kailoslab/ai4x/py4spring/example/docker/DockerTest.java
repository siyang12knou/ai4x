package com.kailoslab.ai4x.py4spring.example.docker;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.kailoslab.ai4x.docker.DockerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DockerTest {

    private final DockerService dockerService;

    @EventListener(ApplicationStartedEvent.class)
    public void test() {
        log.info("{}'s health is {}", dockerService.getHost(), dockerService.isDockerHealth());
        List<Image> imageList = dockerService.getImageList();
        imageList.forEach(image -> log.info(image.toString()));
        List<Container> containerList = dockerService.getContainerList();
        containerList.forEach(container -> log.info(container.toString()));
        String containerId = "54320da29846075385b05901a53bcd58983d55be8763ff36d702f426e7ba115d";
        log.info("{}-{}", containerId, dockerService.removeContainer(containerId));
    }
}
