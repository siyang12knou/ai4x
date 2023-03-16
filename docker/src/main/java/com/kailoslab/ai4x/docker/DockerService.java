package com.kailoslab.ai4x.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.time.Duration;
import java.util.List;

public class DockerService {

    private final DockerProperties properties;
    private final DockerClient dockerClient;

    public DockerService(DockerProperties properties) {
        this.properties = properties;
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(properties.getHost())
                .withDockerTlsVerify(properties.getTls())
                .withDockerCertPath(properties.getTlsCert())
                .withRegistryUsername(properties.getRegistry().getUsername())
                .withRegistryPassword(properties.getRegistry().getPassword())
                .withRegistryEmail(properties.getRegistry().getEmail())
                .withRegistryUrl(properties.getRegistry().getUrl())
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(properties.getMax())
                .connectionTimeout(Duration.ofSeconds(properties.getTimeout()))
                .responseTimeout(Duration.ofSeconds(properties.getTimeout()))
                .build();

        dockerClient = DockerClientImpl.getInstance(config, httpClient);
    }

    public boolean isDockerHealth() {
        try {
            dockerClient.pingCmd().exec();
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    public List<Image> getImageList() {
        return dockerClient.listImagesCmd().withShowAll(true).exec();
    }

    public List<Container> getContainerList() {
        return dockerClient.listContainersCmd()
                .withShowAll(true)
                .withShowSize(true)
                .exec();
    }

    public String createContainer(String name, String image) {
        try {
            CreateContainerResponse response = dockerClient.createContainerCmd(image).withName(name).exec();
            return response.getId();
        } catch (NotFoundException | ConflictException ex) {
            return null;
        }
    }

    public InspectContainerResponse.ContainerState getContainerState(String containerId) {
        try {
            return dockerClient.inspectContainerCmd(containerId).exec().getState();
        } catch (NotFoundException ex) {
            return null;
        }
    }

    public boolean startContainer(String containerId) {
        try {
            dockerClient.startContainerCmd(containerId).exec();
            return true;
        } catch (NotFoundException | NotModifiedException ex) {
            return false;
        }
    }

    public boolean stopContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
            return true;
        } catch (NotFoundException | NotModifiedException ex) {
            return false;
        }
    }

    public boolean restartContainer(String containerId) {
        try {
            dockerClient.restartContainerCmd(containerId).exec();
            return true;
        } catch (NotFoundException | NotModifiedException ex) {
            return false;
        }
    }

    public boolean removeContainer(String containerId) {
        stopContainer(containerId);
        try {
            dockerClient.removeContainerCmd(containerId).exec();
            return true;
        } catch (NotFoundException | NotModifiedException ex) {
            return false;
        }
    }

    public String getHost() {
        return properties.getHost();
    }
}
