package com.kailoslab.ai4x.py4spring.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.kailoslab.ai4x")
public class Ai4xExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(Ai4xExampleApplication.class, args);
    }

}
