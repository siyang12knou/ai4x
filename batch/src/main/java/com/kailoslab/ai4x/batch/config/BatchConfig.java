package com.kailoslab.ai4x.batch.config;

import com.kailoslab.ai4x.utils.Ai4xUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Slf4j
@AllArgsConstructor
@Configuration
@EnableAutoConfiguration
public class BatchConfig {

    public static final String SCHEDULER_YML = "scheduler.yml";

    private final DataSource dataSource;
    private final ApplicationContext applicationContext;
    private final QuartzProperties quartzProperties;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {

        BatchJobFactory jobFactory = new BatchJobFactory();
        jobFactory.setApplicationContext(applicationContext);

        Properties properties = new Properties();
        if(quartzProperties.getProperties().isEmpty()) {
            try {
                properties = Ai4xUtils.loadYaml(new ClassPathResource(SCHEDULER_YML));
                quartzProperties.getProperties().putAll(Ai4xUtils.convert(properties));
            } catch (IOException e) {
                log.error("Cannot find %s".formatted(SCHEDULER_YML), e);
            }
        } else {
            properties.putAll(quartzProperties.getProperties());
        }

        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setDataSource(dataSource);
        factory.setQuartzProperties(properties);
        factory.setJobFactory(jobFactory);
        return factory;
    }
}