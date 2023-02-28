package com.kailoslab.ai4x.app.beantest;

import org.springframework.stereotype.Component;

@Component
public class HelloService {
    public String getHello() {
        return "Hello";
    }
}
