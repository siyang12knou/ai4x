package com.kailoslab.ai4x.alarm;

import com.kailoslab.ai4x.alarm.db.AlarmHistoryRepository;
import com.kailoslab.ai4x.alarm.db.entity.AlarmHistory;
import com.kailoslab.ai4x.alarm.db.entity.AlarmObjectType;
import com.kailoslab.ai4x.alarm.db.entity.AlarmThreshold;
import com.kailoslab.ai4x.event.EventBroadcastService;
import com.kailoslab.ai4x.utils.Ai4xUtils;
import com.kailoslab.ai4x.utils.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;

import static com.kailoslab.ai4x.alarm.AlarmUtils.getAlarmLevelForNumber;

@Service
@Slf4j
public class AlarmService implements ApplicationListener<AlarmRequestEvent> {
    private final AlarmHistoryRepository alarmRepo;
    private final Executor async;
    private final EventBroadcastService broadcaster;
    private final List<AlarmHelper> helpers;
    private final AlarmUtils alarmUtils;

    public AlarmService(AlarmHistoryRepository alarmRepo,
                        Executor async,
                        EventBroadcastService broadcaster,
                        List<AlarmHelper> helpers,
                        AlarmUtils alarmUtils) {
        this.alarmRepo = alarmRepo;
        this.async = async;
        this.broadcaster = broadcaster;
        this.helpers = helpers;
        this.alarmUtils = alarmUtils;
    }

    @Override
    public void onApplicationEvent(AlarmRequestEvent event) {
        if (isEntity(event.getEntity())) {
            List<AlarmObjectType> objectTypes = alarmUtils.getAlarmObjectTypes(event.getEntity());
            if (objectTypes != null && objectTypes.size() > 0) {
                for (AlarmObjectType objectType : objectTypes) {
                    async.execute(() -> checkAlarm(objectType, event));
                }
            }
        }
    }

    private boolean isEntity(Object source) {
        return source != null && source.getClass().isAnnotationPresent(Entity.class);
    }

    private void checkAlarm(AlarmObjectType objectType, AlarmRequestEvent event) {
        Field field = getField(objectType.getColumnName(), event.getEntity());
        if (field == null) {
            return;
        }

        try {
            field.setAccessible(true);
            if (field.get(event.getEntity()) == null) {
                log.warn("Cannot find a value of " + objectType.getColumnName());
                return;
            }
        } catch (Throwable e) {
            log.warn("Cannot get a value of " + objectType.getColumnName());
            return;
        }
        if (objectType.getEnabled()) {
            if (objectType.getAlarmType() == null || objectType.getAlarmType() == AlarmType.Number.ordinal()) {
                checkNumberAlarm(objectType, event, field);
            } else if (objectType.getAlarmType() == AlarmType.Boolean.ordinal()) {
                checkBooleanAlarm(objectType, event, field);
            } else if (objectType.getAlarmType() == AlarmType.Diff.ordinal()) {
                checkDiffAlarm(objectType, event, field);
            }
        }
    }

    private void checkNumberAlarm(AlarmObjectType objectType, AlarmRequestEvent event, Field field) {
        try {
            float value = Ai4xUtils.convertToFloat(field.get(event.getEntity()));
            int currentLevel = getAlarmLevelForNumber(value, objectType.getAlarmThreshold());
            fireAlarm(objectType, event, field, currentLevel, value);
        } catch (Throwable e) {
            log.error("Cannot check a alarm of Number for " + field.getName() + " in " + event.getEntity().getClass().getName());
        }
    }

    private void checkBooleanAlarm(AlarmObjectType objectType, AlarmRequestEvent event, Field field) {
        try {
            boolean value = Ai4xUtils.convertToBoolean(field.get(event.getEntity()));
            boolean criticalValue = false;
            List<AlarmThreshold> threshold = objectType.getAlarmThreshold();
            if (threshold != null && threshold.size() > 0) {
                for (AlarmThreshold alarmThreshold : threshold) {
                    if (alarmThreshold.getId().getLevel() == AlarmLevel.Critical.ordinal()) {
                        criticalValue = alarmThreshold.getValue().intValue() > 0;
                    }
                }
            }

            int currentLevel = getAlarmLevelForBoolean(value, criticalValue);
            fireAlarm(objectType, event, field, currentLevel, value ? 1 : 0);
        } catch (Throwable e) {
            log.error("Cannot check a alarm of Boolean for " + field.getName() + " in " + event.getEntity().getClass().getName());
        }
    }

    private void checkDiffAlarm(AlarmObjectType objectType, AlarmRequestEvent event, Field field) {
        try {
            float before = Ai4xUtils.convertToFloat(field.get(event.getBefore()));
            float current = Ai4xUtils.convertToFloat(field.get(event.getEntity()));
            float value = Math.abs(before - current);
            int currentLevel = getAlarmLevelForDiff(value, objectType.getAlarmThreshold());
            fireAlarm(objectType, event, field, currentLevel, value);
        } catch (Throwable e) {
            log.error("Cannot check a alarm of Diff for " + field.getName() + " in " + event.getEntity().getClass().getName());
        }
    }

    private void fireAlarm(AlarmObjectType objectType, AlarmRequestEvent event, Field field, int currentLevel, float value) {
        try {
            String object = getObjectId(event.getEntity());

            List<AlarmHistory> beforeList = alarmRepo.findByObjectTypeAndPropertyAndObjectAndClearFalseOrderByCreateTimeDesc(objectType.getId().getObjectType(), objectType.getId().getProperty(), object);
            AlarmHistory before = beforeList.size() > 0 ? beforeList.get(0) : null;
            if (before != null) {
                if (currentLevel != before.getLevel()) {
                    LocalDateTime clearDatetime = LocalDateTime.now();
                    for (AlarmHistory alarmHistory : beforeList) {
                        alarmHistory.setClear(true);
                        alarmHistory.setClearTime(clearDatetime);
                    }

                    alarmRepo.saveAll(beforeList);

                    if (currentLevel == AlarmLevel.Normal.ordinal()) {
                        broadcaster.broadcast(new AlarmClearEvent(beforeList));
                    }

                    log.info("Cleared a alarm: " + before);
                } else {
                    return;
                }
            }

            if (currentLevel != AlarmLevel.Normal.ordinal()) {
                AlarmHistory alarm = createAlarmHistory(objectType, object, currentLevel, value, event);

                beforeSaveAlarmHistory(event, objectType, alarm);
                alarm = alarmRepo.save(alarm);
                afterSaveAlarmHistory(event, objectType, alarm);

                broadcaster.broadcast(new AlarmEvent(alarm));
                log.info("Fire a alarm: " + alarm);
            }
        } catch (Throwable e) {
            log.error("Cannot check a alarm of number for " + field.getName() + " in " + event.getEntity().getClass().getName());
        }
    }

    private int getAlarmLevelForBoolean(boolean bool, boolean criticalValue) {
        if (bool == criticalValue) {
            return AlarmLevel.Critical.ordinal();
        } else {
            return AlarmLevel.Normal.ordinal();
        }
    }

    private int getAlarmLevelForDiff(float diff, List<AlarmThreshold> threshold) {
        return getAlarmLevelForNumber(diff, threshold);
    }

    private String getMessage(AlarmHistory history, AlarmObjectType obj) {
        if (history.getLevel() == AlarmLevel.Normal.ordinal()) {
            return AlarmConstants.NORMAL_MESSAGE;
        }
        // 값이 없으면 null
        if (obj == null || StringUtils.isEmpty(obj.getMessageFormat())) {
            return null;
        }

        String message = obj.getMessageFormat();
        List<String> binds = new ArrayList<>();
        Matcher bindMatcher = AlarmConstants.BIND_PATTERN.matcher(message);
        while (bindMatcher.find()) {
            binds.add(bindMatcher.group(1)); // Group one is necessary because of the brackets in the pattern
        }

        if (binds.size() > 0) {
            for (String bindColumn : binds) {
                Method get = AlarmConstants.GET_METHOD.get(bindColumn);
                if (get == null) {
                    message = message.replace(AlarmConstants.BIND_START + bindColumn + AlarmConstants.BIND_END, bindColumn + "(?)");
                } else {
                    Object object;
                    if (get.getDeclaringClass().equals(AlarmHistory.class)) {
                        object = history;
                    } else {
                        object = obj;
                    }

                    try {
                        message = message.replace(AlarmConstants.BIND_START + bindColumn + AlarmConstants.BIND_END,
                                get.invoke(object).toString());
                    } catch (Throwable e) {
                        message = message.replace(AlarmConstants.BIND_START + bindColumn + AlarmConstants.BIND_END, bindColumn + "(?)");
                    }
                }
            }

        }
        return message;
    }

    private Field getField(String column, Object entity) {
        if (column == null) {
            return null;
        }

        Class<?> clazz = entity.getClass();
        try {
            return clazz.getDeclaredField(column);
        } catch (NoSuchFieldException | SecurityException e) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Column col = field.getDeclaredAnnotation(Column.class);
                if (col != null && col.name().equals(column)) {
                    return field;
                }
            }

            try {
                return clazz.getDeclaredField(Ai4xUtils.convertToCamelCase(column));
            } catch (NoSuchFieldException | SecurityException e1) {
                log.warn("Cannot find a field of column(" + column + ")");
                return null;
            }
        }
    }

    private String getObjectId(Object entity) {
        Class<?> clazz = entity.getClass();
        StringBuilder sb = new StringBuilder();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getDeclaredAnnotation(Id.class) != null) {
                try {
                    if (sb.length() > 0) {
                        sb.append(Constants.COLON);
                    }

                    field.setAccessible(true);
                    sb.append(field.get(entity));
                } catch (Throwable e) {
                    log.debug("Cannot get a data for " + field.getName() + " in " + entity.getClass().getName());
                }
            }

            if (field.getDeclaredAnnotation(EmbeddedId.class) != null) {
                try {
                    field.setAccessible(true);
                    Object id = field.get(entity);
                    Field[] embeddedFields = id.getClass().getDeclaredFields();
                    for (Field embeddedField : embeddedFields) {
                        if (embeddedField.getDeclaredAnnotation(AlarmObjectColumn.class) != null) {
                            embeddedField.setAccessible(true);
                            sb.append(embeddedField.get(id));
                        }
                    }
                } catch (Throwable e) {
                    log.debug("Cannot get a data for " + field.getName() + " in " + entity.getClass().getName());
                }
            }
        }

        return sb.toString();
    }

    private AlarmHistory createAlarmHistory(AlarmObjectType objectType, String object, int currentLevel, float value, AlarmRequestEvent event) {
        LocalDateTime now = Ai4xUtils.nowLocalDateTime();

        AlarmHistory history = new AlarmHistory();
        history.setId(Ai4xUtils.getUUID());
        history.setCreateTime(now);
        history.setObjectType(objectType.getId().getObjectType());
        history.setProperty(objectType.getId().getProperty());
        history.setObject(object);
        history.setLevel(currentLevel);
        history.setValue(value);
        history.setMessage(getMessage(history, objectType));
        history.setNdId(event.getDomain());

        if (currentLevel == AlarmLevel.Normal.ordinal()) {
            history.setClear(true);
            history.setClearTime(now);
        } else {
            history.setClear(false);
            history.setClearTime(null);
        }

        return history;
    }

    private void beforeSaveAlarmHistory(AlarmRequestEvent event, AlarmObjectType objectType, AlarmHistory history) {
        if (helpers != null) {
            for (AlarmHelper helper : helpers) {
                if (helper.isSupportedObjectType(objectType)) {
                    helper.beforeSaveAlarmHistory(event, objectType, history);
                }
            }
        }
    }

    private void afterSaveAlarmHistory(AlarmRequestEvent event, AlarmObjectType objectType, AlarmHistory history) {
        if (helpers != null) {
            for (AlarmHelper helper : helpers) {
                if (helper.isSupportedObjectType(objectType)) {
                    helper.afterSaveAlarmHistory(event, objectType, history);
                }
            }
        }
    }
}
