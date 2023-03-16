package com.kailoslab.ai4x.event;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.kailoslab.ai4x.event.stomp.StompProperties;
import com.kailoslab.ai4x.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class BroadcastEvent<T> extends ApplicationEvent {

    public static BroadcastEvent<?> create(String topic, Object payload, StompProperties stompProperties) throws Throwable {
        Class<? extends BroadcastEvent> eventClass = getBroadcastEventClass(topic, stompProperties);
        return create(eventClass, payload);
    }

    public static BroadcastEvent<?> create(Class<? extends BroadcastEvent> eventClass, Object payload) throws Throwable{
        if(eventClass == null || payload == null) {
            throw new IllegalArgumentException("Cannot parse a class of source that is null.");
        }

        Type superType = eventClass.getGenericSuperclass();
        if (superType instanceof ParameterizedType) {
            Type sourceType = ((ParameterizedType) superType).getActualTypeArguments()[0];
            JavaType sourceJavaType = Constants.JSON_MAPPER.getTypeFactory().constructType(sourceType);
            Constructor<?> constructor = eventClass.getConstructor(sourceJavaType.getRawClass());
            constructor.setAccessible(true);

            Object[] value;
            if(payload instanceof String) {
                value = Constants.JSON_MAPPER.readValue((String)payload, sourceJavaType);
            } else {
                value = Constants.JSON_MAPPER.convertValue(payload, sourceJavaType);
            }

            if(value != null) {
                return (BroadcastEvent<?>) constructor.newInstance(value);
            }
        }

        throw new IllegalArgumentException("Cannot parse a class of source.");
    }

    public static Class<? extends BroadcastEvent> getBroadcastEventClass(String topic, StompProperties stompProperties) {
        String classTopic = stompProperties.removeTopicPrefix(topic);
        String className = StringUtils.replace(classTopic, Constants.SLASH, Constants.DOT);
        if (className.startsWith(Constants.DOT)) {
            className = className.substring(Constants.DOT.length());
        }

        try {
            return (Class<? extends BroadcastEvent>) Class.forName(className);
        } catch (Throwable e) {
            return null;
        }
    }

    public static Class<?> getBroadcastEventSourceClass(String topic, StompProperties stompProperties) {
        Class<? extends BroadcastEvent> eventClass = getBroadcastEventClass(topic, stompProperties);
        if(eventClass == null) {
            return null;
        }

        Type superType = eventClass.getGenericSuperclass();
        if (superType instanceof ParameterizedType) {
            Type sourceType = ((ParameterizedType) superType).getActualTypeArguments()[0];
            return TypeFactory.rawClass(sourceType);
        } else {
            return null;
        }
    }

    public static boolean isTopicOfBroadcastEvent(String topic, StompProperties stompProperties) {
        return getBroadcastEventClass(topic, stompProperties) != null;
    }

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public BroadcastEvent(T source) {
        super(source);
    }

    public T getEventSource() {
        return (T) getSource();
    }
}
