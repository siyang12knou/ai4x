package com.kailoslab.ai4x.py4spring.example.beantest;

import org.springframework.stereotype.Component;

@Component
public class HelloService {
    public String sayHello() {
        return "Hello";
    }
}
