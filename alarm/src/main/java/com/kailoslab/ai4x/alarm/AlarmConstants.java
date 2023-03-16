package com.kailoslab.ai4x.alarm;

import com.kailoslab.ai4x.alarm.db.entity.AlarmHistory;
import com.kailoslab.ai4x.alarm.db.entity.AlarmObjectType;
import com.kailoslab.ai4x.utils.Ai4xUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AlarmConstants {
    public static final String COLLECTOR_SYSTEM = "COLLECTOR";
    public static final String EMPTY_SOURCE = "-";
    public static final String NORMAL_MESSAGE = "정상상태가 되었습니다.";
    public static final String BIND_START = "{{";
    public static final String BIND_END = "}}";
    public static final Pattern BIND_PATTERN = Pattern.compile(Pattern.quote(BIND_START) + "(.*?)" + Pattern.quote(BIND_END));
    public static final Map<String, Method> GET_METHOD;

    static {
        Map<String, Method> methods = new HashMap<>();
        for (Method method : AlarmObjectType.class.getMethods()) {
            if (method.getName().startsWith("get")) {
                String name = method.getName();
                if (!name.equals("get")) {
                    name = name.substring(3);
                    methods.put(Ai4xUtils.convertToSnakeCase(name), method);
                }
            }
        }

        for (Method method : AlarmHistory.class.getMethods()) {
            if (method.getName().startsWith("get")) {
                String name = method.getName();
                if (!name.equals("get")) {
                    name = name.substring(3);
                    methods.put(Ai4xUtils.convertToSnakeCase(name), method);
                }
            }
        }

        GET_METHOD = Collections.unmodifiableMap(methods);
    }
}
