package com.kailoslab.ai4x.commons.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kailoslab.ai4x.commons.annotation.Title;
import com.kailoslab.ai4x.commons.data.PropertiesRepository;
import com.kailoslab.ai4x.commons.data.dto.PropertiesFormatDto;
import com.kailoslab.ai4x.commons.data.entity.PropertiesEntity;
import com.kailoslab.ai4x.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertiesService {
    private final TitleService titleService;
    private final PropertiesRepository propertiesRepository;

    public List<PropertiesFormatDto> parsePropertiesFormat(String serviceName, String instanceName, Parameter[] parameters) {

        return Arrays.stream(parameters).map(parameter ->
                parsePropertiesFormat(serviceName, instanceName, parameter.getName(), parameter.getType())
        ).collect(Collectors.toList());
    }

    public PropertiesFormatDto parsePropertiesFormat(String serviceName, String instanceName, String propertiesName, Class<?> propertiesClass) {
        PropertiesFormatDto formatDto = new PropertiesFormatDto();
        formatDto.setServiceName(serviceName);
        formatDto.setInstanceName(instanceName);
        formatDto.setPropertiesName(propertiesName);
        Title title = propertiesClass.getDeclaredAnnotation(Title.class);
        if(title == null) {
            formatDto.setTitle(propertiesName);
        } else {
            formatDto.setTitle(titleService.getTitle(title, propertiesClass));
        }


        return formatDto;
    }

    @Transactional
    public void saveProperties(String serviceName, String instanceName, Parameter[] parameters) {
        List<PropertiesEntity> oldPropertiesEntities = propertiesRepository.findAllByServiceNameAndInstanceName(serviceName, instanceName);
        Map<String, String> properties = new HashMap<>(oldPropertiesEntities.size());
        oldPropertiesEntities.forEach(propertiesEntity -> properties.put(propertiesEntity.getId(), propertiesEntity.getProperties()));

        propertiesRepository.deleteAllByServiceNameAndInstanceName(serviceName, instanceName);

        if(StringUtils.isNoneEmpty(serviceName, instanceName) && ObjectUtils.isNotEmpty(parameters)) {
            List<PropertiesEntity> propertiesEntities = IntStream.range(0, parameters.length).mapToObj(index -> {
                Parameter parameter = parameters[index];
                PropertiesEntity propertiesEntity = new PropertiesEntity();
                String propertiesName = parameter.getName();
                String propertiesType = parameter.getType().getName();
                String id = propertiesRepository.generateId(serviceName, instanceName, propertiesName);
                propertiesEntity.setId(id);
                propertiesEntity.setServiceName(serviceName);
                propertiesEntity.setInstanceName(instanceName);
                propertiesEntity.setPropertiesName(propertiesName);
                propertiesEntity.setPropertiesType(propertiesType);
                propertiesEntity.setOrdinal(index);
                propertiesEntity.setProperties(properties.get(id));

                return propertiesEntity;
            }).collect(Collectors.toList());

            propertiesRepository.saveAll(propertiesEntities);
        }
    }

    public void saveProperties(String serviceName, String instanceName, Map<String, Object> propertiesMap) {
        List<PropertiesEntity> propertiesEntities = propertiesMap.keySet().stream().map(propertiesName -> {
            Optional<PropertiesEntity> propertiesEntityOptional = propertiesRepository.findByServiceNameAndInstanceNameAndPropertiesName(serviceName, instanceName, propertiesName);
            if(propertiesEntityOptional.isPresent()) {
                PropertiesEntity propertiesEntity = propertiesEntityOptional.get();
                Object properties = propertiesMap.get(propertiesName);
                if(ObjectUtils.isNotEmpty(properties)) {
                    try {
                        propertiesEntity.setProperties(Constants.JSON_MAPPER.writeValueAsString(properties));
                        return propertiesEntity;
                    } catch (JsonProcessingException e) {
                        log.error("Cannot parse properties.", e);
                    }
                }
            }

            return null;
        }).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());

        propertiesRepository.saveAll(propertiesEntities);
    }

    public Map<String, Object> getProperties(String serviceName, String instanceName) {
        List<PropertiesEntity> propertiesEntities = propertiesRepository.findAllByServiceNameAndInstanceName(serviceName, instanceName);
        Map<String, Object> result = new LinkedHashMap<>(propertiesEntities.size());
        propertiesEntities.forEach(propertiesEntity -> {
           String propertiesType = propertiesEntity.getPropertiesType();
           String properties = propertiesEntity.getProperties();
           if(StringUtils.isNoneEmpty(propertiesType, properties)) {
               try {
                   Class<?> propertiesTypeClass = Class.forName(propertiesType);
                   result.put(propertiesEntity.getPropertiesName(), Constants.JSON_MAPPER.readValue(properties, propertiesTypeClass));
               } catch (ClassNotFoundException | JsonProcessingException e) {
                   log.error("Cannot load a class (%s) for properties.".formatted(propertiesType), e);
               }
           }
        });

        return result;
    }
}
