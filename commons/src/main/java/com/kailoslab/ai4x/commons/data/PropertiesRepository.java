package com.kailoslab.ai4x.commons.data;

import com.kailoslab.ai4x.commons.data.entity.PropertiesEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PropertiesRepository extends CrudRepository<PropertiesEntity, String> {
    default String generateId(String serviceName, String instanceName, String propertiesName) {
        return "%s.%s.%s".formatted(StringUtils.trimToEmpty(serviceName), StringUtils.trimToEmpty(instanceName), StringUtils.trimToEmpty(propertiesName));
    }

    default List<PropertiesEntity> findAllByServiceNameAndInstanceName(String serviceName, String instanceName) {
        return findAllByIdStartsWithOrderByOrdinal("%s.%s".formatted(serviceName, instanceName));
    }

    default void deleteAllByServiceNameAndInstanceName(String serviceName, String instanceName) {
        deleteAllByIdStartsWith("%s.%s".formatted(serviceName, instanceName));
    }

    default Optional<PropertiesEntity> findByServiceNameAndInstanceNameAndPropertiesName(String serviceName, String instanceName, String propertiesName) {
        return findById(generateId(serviceName, instanceName, propertiesName));
    }

    List<PropertiesEntity> findAllByIdStartsWithOrderByOrdinal(String id);
    void deleteAllByIdStartsWith(String id);
}
