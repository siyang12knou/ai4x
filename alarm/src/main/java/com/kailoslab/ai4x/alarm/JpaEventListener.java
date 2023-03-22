package com.kailoslab.ai4x.alarm;

import com.kailoslab.ai4x.alarm.db.AlarmObjectTypeRepository;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.scheduling.annotation.Async;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class JpaEventListener {

    private final ApplicationContext applicationContext;
    private final AlarmObjectTypeRepository alarmObjectTypeRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    @Value("${ai4x.alarm.domain:}")
    private String domain;
    private final Map<Object, Object> beforeList = Collections.synchronizedMap(new HashMap<>());

    @PostPersist
    @PostUpdate
    @Async
    public void fireAlarmRequestEvent(Object entity) {
        if(entity.getClass().getDeclaredAnnotation(Entity.class) != null) {
            String tableName = getTableName(entity);
            if (alarmObjectTypeRepository.existsByTableNameAndAlarmTypeNot(tableName, AlarmType.Diff.ordinal())) {
                AlarmRequestEvent event;
                if(beforeList.containsKey(entity)) {
                    event = new AlarmRequestEvent(domain, beforeList.remove(entity), entity);
                } else {
                    event = new AlarmRequestEvent(domain, entity);
                }
                applicationEventPublisher.publishEvent(event);
            }
        }
    }

    @PrePersist
    @PreUpdate
    public void fireAlarmRequestEventDiff(Object entity) {
        if(entity.getClass().getDeclaredAnnotation(Entity.class) != null) {
            String tableName = getTableName(entity);
            if (alarmObjectTypeRepository.existsByTableNameAndAlarmType(tableName, AlarmType.Diff.ordinal())) {
                Object before = getCurrentData(entity);
                if(before != null) {
                    beforeList.put(entity, before);
                }
            }
        }
    }

    private String getTableName(Object entity) {
        String tableName = null;
        Table table = entity.getClass().getDeclaredAnnotation(Table.class);
        if(table != null) {
            tableName = table.name();
        }

        if(StringUtils.isEmpty(tableName)) {
            tableName = entity.getClass().getSimpleName();
        }

        return tableName;
    }

    private Object getCurrentData(Object entity) {
        Repositories repositories = applicationContext.getBean(Repositories.class);
        Optional<Object> repositoryOptional = repositories.getRepositoryFor(entity.getClass());
        if(repositoryOptional.isPresent()
                && JpaRepository.class.isAssignableFrom(repositoryOptional.get().getClass())) {
            JpaRepository repository = (JpaRepository) repositoryOptional.get();
            Object id = getId(entity);
            if(id != null) {
                return repository.findById(id);
            }
        }

        return null;
    }

    private Object getId(Object entity) {
        Class<?> clazz = entity.getClass();
        Object id = null;
        IdClass idClass = clazz.getDeclaredAnnotation(IdClass.class);
        try {
            if(idClass != null) {
                Class<?> idClazz = idClass.value();
                Field[] idClassFields = idClazz.getDeclaredFields();
                List<Field> idFields = Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Id.class)).toList();
                    Object newIdObject = idClazz.getConstructor().newInstance();
                    for (Field idClassField : idClassFields) {
                        for(Field field : idFields) {
                            if(field.getName().equals(idClassField.getName())) {
                                idClassField.set(newIdObject, field.get(entity));
                            }
                        }
                    }
                    id = newIdObject;
            } else {
                Optional<Field> embeddedIdFieldOptional = Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(EmbeddedId.class)).findFirst();
                if(embeddedIdFieldOptional.isPresent()) {
                    id = embeddedIdFieldOptional.get().get(entity);
                } else {
                    Optional<Field> idFieldOptional = Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Id.class)).findFirst();
                    if(idFieldOptional.isPresent()) {
                        id = idFieldOptional.get().get(entity);
                    }
                }
            }

            return id;

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }
}
