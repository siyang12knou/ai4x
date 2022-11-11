package com.kailoslab.ai4x.commons.utils;

import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

/**
 * 커스텀 Yaml 파일을 읽어 Spring의 @Value로 참조할 수 있도록 지원하는 클래스
 *
 * <pre>{@code
 * @Service
 * @PropertySource(value = "classpath:config-prop.yml", factory = YamlPropertySourceFactory.class)
 * public class SampleService {
 *
 *     @Value("${sample.max.length}")
 *     private int maxLength;
 * }
 * }</pre>
 */
public class YamlPropertySourceFactory  implements PropertySourceFactory {

    /**
     * Yaml 프로퍼티를 로딩하여 제공하는 {@link PropertySource}를 생성
     *
     * @param name     프로퍼티 소스 이름
     * @param resource Yaml 프로퍼티 파일에 대한 리소스
     * @return {@link PropertySource}
     * @throws IOException 리소스를 로드하는데 실패할 때 발생
     */
    @Override
    @Nonnull
    public PropertySource<?> createPropertySource(String name, EncodedResource resource)
            throws IOException {
        Properties properties = Utils.loadYaml(resource.getResource());
        return new PropertiesPropertySource(name != null ? name :
                Objects.requireNonNull(resource.getResource().getFilename(), "Invalid a resource %s".formatted(resource)),
                properties);
    }
}