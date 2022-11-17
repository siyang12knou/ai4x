package com.kailoslab.ai4x.commons.config;

import com.kailoslab.ai4x.commons.data.converter.ListToStringConverter;
import com.kailoslab.ai4x.commons.data.converter.MapToStringConverter;
import com.kailoslab.ai4x.commons.data.converter.StringToListConverter;
import com.kailoslab.ai4x.commons.data.converter.StringToMapConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableJdbcRepositories
@EnableJdbcAuditing
public class JdbcConfiguration extends AbstractJdbcConfiguration {
    @Override
    protected List<?> userConverters() {
        return Arrays.asList(new ListToStringConverter(),
                new StringToListConverter(),
                new MapToStringConverter(),
                new StringToMapConverter());
    }
}
