package com.kailoslab.ai4x.app;

import com.kailoslab.ai4x.py4spring.Py4SpringConfiguration;
import com.kailoslab.ai4x.py4spring.controller.Py4SpringDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
class Ai4xApplicationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ai4xApplication.class));

    @Test
    void contextLoads() {
    }

    @Test
    public void defaultServiceBacksOff() {
        this.contextRunner.withUserConfiguration(Py4SpringConfiguration.class).run((context) -> {
            assertThat(context).hasSingleBean(Py4SpringDispatcher.class);
            assertThat(context.getBean(Py4SpringDispatcher.class))
                    .isSameAs(context.getBean(Py4SpringConfiguration.class).py4SpringDispatcher());
        });
    }
}
