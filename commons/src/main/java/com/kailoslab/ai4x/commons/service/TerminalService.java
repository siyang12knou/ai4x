package com.kailoslab.ai4x.commons.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@Slf4j
@Service
public class TerminalService {

    Path defaultWorkingDir = Paths.get("");

    public Future<Integer> execute(String... command) {
        return execute(defaultWorkingDir, command);
    }

    public Future<Integer> execute(Consumer<String> consumer, String... command) {
        return execute(defaultWorkingDir, consumer, command);
    }

    public Future<Integer> execute(Path workingDir, String... command) {
        return execute(workingDir, this::println, command);
    }

    public Future<Integer> execute(Path workingDir, Consumer<String> consumer, String... command) {

        return Executors.newSingleThreadExecutor().submit(() -> {
            if(!Files.exists(workingDir)) {
                try {
                    Files.createDirectories(workingDir);
                } catch (IOException e) {
                    log.error("Cannot create a working directory: " + workingDir.toAbsolutePath(), e);
                }
            }

            ProcessBuilder builder = new ProcessBuilder();
            builder.command(command);
            builder.directory(workingDir.toFile());
            try {
                Process process = builder.start();
                StreamGobbler streamGobbler =
                        new StreamGobbler(process.getInputStream(), consumer);
                Executors.newSingleThreadExecutor().submit(streamGobbler);
                int exitCode = process.waitFor();
                if(exitCode == -1) {
                    log.error("Failure execute a command: " + String.join(" ", command));
                }

                return exitCode;
            } catch (IOException | InterruptedException e) {
                log.error("Failure execute a command: " + String.join(" ", command), e);
                return -1;
            }
        });
    }

    public int executeSync(String... command) {
        try {
            return execute(command).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failure execute a command: " + String.join(" ", command), e);
            return -1;
        }
    }

    public int executeSync(Consumer<String> consumer, String... command) {
        try {
            return execute(consumer, command).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failure execute a command: " + String.join(" ", command), e);
            return -1;
        }
    }

    public int executeSync(Path workingDir, String... command) {
        try {
            return execute(workingDir, command).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failure execute a command: " + String.join(" ", command), e);
            return -1;
        }
    }

    public int executeSync(Path workingDir, Consumer<String> consumer, String... command) {
        try {
            return execute(workingDir, consumer, command).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failure execute a command: " + String.join(" ", command), e);
            return -1;
        }
    }

    private record StreamGobbler(InputStream inputStream,
                                 Consumer<String> consumer) implements Runnable {

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }

    private void println(String text) {
        log.info(text);
    }
}
