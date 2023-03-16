package com.kailoslab.ai4x.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Ai4xUtils {

    private static final Logger logger = LoggerFactory.getLogger(Ai4xUtils.class);
    private static final InetAddress OUTBOUND_ADDRESS = getLocalHostLANAddress();

    /**
     * Yaml 파일을 읽는다.
     *
     * @param resource Yaml 파일에 대한 {@link Resource}
     * @return Yaml 파일에 대한 프로퍼티 객체
     */
    public static Properties loadYaml(Resource resource) throws FileNotFoundException {
        try {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource);
            factory.afterPropertiesSet();

            return factory.getObject();
        } catch (IllegalStateException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof FileNotFoundException) throw (FileNotFoundException) cause;
            throw ex;
        }
    }

    public static String stringifyJson(Object object) {
        try {
            return com.kailoslab.ai4x.utils.Constants.JSON_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Cannot convert object to string." + object);
            throw new IllegalArgumentException("Cannot convert object to string." + object);
        }
    }

    public static Map<String, Object> mapJson(Object object) {
        return Constants.JSON_MAPPER.convertValue(object, new TypeReference<>() {});
    }

    public static <T> T parseJson(String string) {
        try {
            return Constants.JSON_MAPPER.readValue(string, new TypeReference<>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Cannot convert string to object " + string);
            throw new IllegalArgumentException("Cannot convert string" + string);
        }
    }

    public static HashMap<String, String> convert(Properties prop) {
        return prop.entrySet().stream().collect(
                Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> String.valueOf(e.getValue()),
                        (prev, next) -> next, HashMap::new
                ));
    }

    public static String snakeToCamel(String snake) {
        String[] words = snake.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                word = word.isEmpty() ? word : word.toLowerCase();
            } else {
                word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            }
            builder.append(word);
        }
        return builder.toString();
    }

    public static String camelToSnake(String camel) {
        return camel.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    public static String toFirstLowerCase(String text) {
        return Character.toLowerCase(text.charAt(0)) + (text.length() > 1 ? text.substring(1) : "");
    }

    public static String toFirstUpperCase(String text) {
        return Character.toUpperCase(text.charAt(0)) + (text.length() > 1 ? text.substring(1) : "");
    }

    public static <T> T newInstance(Map<String, String> properties, Class<T> clazz) {
        if(ObjectUtils.isEmpty(properties)) {
            return null;
        }

        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();
            Arrays.stream(fields).forEach(field -> {
                JsonProperty jsonProperty = field.getDeclaredAnnotation(JsonProperty.class);
                String keyName;
                if(jsonProperty != null && StringUtils.isNotEmpty(jsonProperty.value())) {
                    keyName = jsonProperty.value().toLowerCase();
                } else {
                    keyName = Ai4xUtils.camelToSnake(field.getName());
                }

                if(StringUtils.isNotEmpty(properties.get(keyName))) {
                    try {
                        Method getMethod = clazz.getMethod("get" + toFirstUpperCase(field.getName()));
                        Class<?> paramType = getMethod.getReturnType();
                        Method setMethod = clazz.getMethod("set" + toFirstUpperCase(field.getName()), paramType);
                        Method valueOf = paramType.getMethod("valueOf", String.class);
                        String paramValue = properties.get(keyName);
                        if (paramType == Boolean.class) {
                            if (StringUtils.equals(paramValue, "0")) {
                                paramValue = "false";
                            } else if (StringUtils.equals(paramValue, "1")) {
                                paramValue = "true";
                            }
                        }

                        setMethod.invoke(instance, valueOf.invoke(null, paramValue));
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
                }
            });

            return instance;

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }

    public static List<String> getScanPackages(ApplicationContext applicationContext) {
        String[] springBootAppBeanName = applicationContext.getBeanNamesForAnnotation(SpringBootApplication.class);
        List<String> scanPackages = new ArrayList<>(Collections.singleton("com.kailoslab.ai4x"));
        Arrays.stream(springBootAppBeanName)
            .forEach(name -> {
                Class<?> applicationClass = applicationContext.getBean(name).getClass();
                ComponentScan componentScan = applicationClass.getAnnotation(ComponentScan.class);
                if(componentScan == null && applicationClass.getSuperclass() != null) {
                    Class<?> applicationSuperClass = applicationClass.getSuperclass();
                    componentScan = applicationSuperClass.getAnnotation(ComponentScan.class);
                }

                if(componentScan == null) {
                    scanPackages.add(applicationContext.getBean(name).getClass().getPackageName());
                } else {
                    for (Class<?> basePackageClass :
                            componentScan.basePackageClasses()) {
                        scanPackages.add(basePackageClass.getPackageName());
                    }

                    Collections.addAll(scanPackages, componentScan.basePackages());
                }
            });

        return scanPackages;
    }

    public static String getString(Object object, String... methodNames) {
        String result = null;
        for(String methodName: methodNames) {
            try {
                Method m = object.getClass().getMethod(methodName, (Class<?>) null);
                Object obj = m.invoke(object, (Object) null);
                if(ObjectUtils.isNotEmpty(obj)) {
                    result = obj.toString();
                    break;
                }
            } catch (Throwable ignored) {
            }
        }

        return result;
    }

    public static Integer getInt(Object object, String... methodNames) {
        int result = 0;
        for(String methodName: methodNames) {
            try {
                Method m = object.getClass().getMethod(methodName, (Class<?>) null);
                Object obj = m.invoke(object, (Object) null);
                if(ObjectUtils.isNotEmpty(obj)) {
                    result = Integer.parseInt(obj.toString());
                    break;
                }
            } catch (Throwable ignored) {}
        }

        return result;
    }

    public static boolean isAnyEmpty(Object ... objects) {
        for (Object object :
                objects) {
            if (ObjectUtils.isEmpty(object)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAllEmpty(Object ... objects) {
        for (Object object :
                objects) {
            if (ObjectUtils.isNotEmpty(object)) {
                return false;
            }
        }

        return true;
    }

    public static String getOutboundIp() {
        if (OUTBOUND_ADDRESS != null) {
            return OUTBOUND_ADDRESS.getHostAddress();
        } else {
            return null;
        }
    }

    public static String getHostName() {
        if (OUTBOUND_ADDRESS != null) {
            return OUTBOUND_ADDRESS.getHostName();
        } else {
            return null;
        }
    }

    public static InetAddress getOutboundInetAddress() {
        return OUTBOUND_ADDRESS;
    }

    private static InetAddress getLocalHostLANAddress() {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {

                        if (inetAddr.isSiteLocalAddress()) {
                            // Found non-loopback site-local address. Return it immediately...
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // Found non-loopback address, but not necessarily site-local.
                            // Store it as a candidate to be returned if site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                            // only the first. For subsequent iterations, candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other non-loopback address.
                // Server might have a non-site-local address assigned to its NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress;
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost() returns...
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<?, ?> convertToMap(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Map) {
            return (Map<?, ?>) object;
        } else {
            try {
                return Constants.JSON_MAPPER.convertValue(object, Map.class);
            } catch (Throwable e) {
                return null;
            }
        }
    }

    public static <T> T convertMapToBean(Class<T> clazz, Map<?, ?> map) throws Exception {
        T bean = clazz.getDeclaredConstructor().newInstance();
        Map<String, Method> setMethods = new HashMap<>();
        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith("set")) {
                setMethods.put(method.getName(), method);
            }
        }

        // set 메서드가 하나 이상 없으면 null 반환
        if (setMethods.size() == 0) {
            return null;
        }

        Method set;
        for (Object key : map.keySet()) {
            String pascal = convertToPascalCase(key.toString());
            set = setMethods.get("set" + pascal);
            if (set == null || set.getParameterCount() != 1) {
                continue;
            }

            try {
                set.invoke(bean, convertTo(set.getParameters()[0].getType(), map.get(key)));
            } catch (Exception ignored) {
                logger.debug(ignored.getMessage() + " in convertMapToBean");
            }
        }

        return bean;
    }

    public static Object convertTo(Class<?> clazz, Object value) {
        if (clazz.equals(Boolean.class)) {
            return convertToBoolean(value);
        } else if (clazz.equals(Byte.class)) {
            return convertToByte(value);
        } else if (clazz.equals(Short.class)) {
            return convertToShort(value);
        } else if (clazz.equals(Integer.class)) {
            return convertToInt(value);
        } else if (clazz.equals(Long.class)) {
            return convertToLong(value);
        } else if (clazz.equals(Float.class)) {
            return convertToFloat(value);
        } else if (clazz.equals(BigInteger.class)) {
            return convertToBigInteger(value);
        } else if (clazz.equals(String.class)) {
            return convertToString(value);
        } else if (clazz.equals(Date.class)) {
            return convertToDate(value);
        } else {
            return Constants.JSON_MAPPER.convertValue(value, clazz);
        }
    }

    public static Boolean convertToBoolean(Object object) {
        if (object == null) {
            return false;
        } else if (object instanceof Number) {
            return ((Number) object).byteValue() > 0;
        } else if (object instanceof Boolean) {
            return (Boolean) object;
        } else {
            return Boolean.parseBoolean(object.toString());
        }
    }

    public static Byte convertToByte(Object object) {
        if (object == null) {
            return 0;
        } else if (object instanceof Number) {
            return ((Number) object).byteValue();
        } else if (object instanceof Boolean) {
            return (byte) ((Boolean) object ? 1 : 0);
        } else {
            return Byte.parseByte(object.toString());
        }
    }

    public static Short convertToShort(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Number) {
            return ((Number) object).shortValue();
        } else {
            return Short.parseShort(object.toString());
        }
    }

    public static Integer convertToInt(Object object) {
        if (object == null) {
            return 0;
        } else if (object instanceof Number) {
            return ((Number) object).intValue();
        } else {
            return Integer.parseInt(object.toString());
        }
    }

    public static Long convertToLong(Object object) {
        if (object == null) {
            return (long) 0;
        } else if (object instanceof Number) {
            return ((Number) object).longValue();
        } else {
            return Long.parseLong(object.toString());
        }
    }

    public static Float convertToFloat(Object object) {
        if (object == null) {
            return 0f;
        } else if (object instanceof Number) {
            return ((Number) object).floatValue();
        } else {
            return Float.parseFloat(object.toString());
        }
    }

    public static BigInteger convertToBigInteger(Object object) {
        if (object == null) {
            return null;
        } else {
            return new BigInteger(object.toString());
        }
    }

    public static String convertToString(Object object) {
        return convertToString(object, null);
    }

    public static String convertToString(Object object, String argSuffix) {
        String suffix = argSuffix;
        if (suffix == null) {
            suffix = "";
        }

        if (object == null) {
            return null;
        } else if (object instanceof Number) {
            return Constants.nf.format(object) + suffix;
        } else if (object instanceof String) {
            String str = object.toString().trim();
            StringBuilder sb = new StringBuilder(str);
            if (!str.endsWith(suffix)) {
                sb.append(suffix);
            }

            return sb.toString();
        } else {
            try {
                return Constants.JSON_MAPPER.writeValueAsString(object);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
    }

    public static Date convertToDate(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("input value is null");
        } else if (object instanceof Date) {
            return (Date) object;
        } else {
            try {
                String str = object.toString();
                return Date.from(LocalDateTime.parse(str,
                                DateTimeFormatter.ofPattern(Constants.df))
                        .atZone(ZoneId.systemDefault()).toInstant());
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public static Date convertToDate(Object object, Date defaultValue) {
        if (object == null) {
            return defaultValue;
        } else {
            return convertToDate(object);
        }
    }

    public static String convertToCamelCase(String string) {
        String pascal = convertToPascalCase(string);
        if (pascal.length() > 0) {
            if (pascal.length() > 1) {
                return Character.toLowerCase(pascal.charAt(0)) + pascal.substring(1);
            } else {
                return pascal.toLowerCase();
            }
        } else {
            return pascal;
        }
    }

    public static String convertToPascalCase(String string) {
        String[] splitStr = string.split("_");
        StringBuilder sb = new StringBuilder();
        for (String str : splitStr) {
            str = str.trim();
            if (str.length() > 0) {
                sb.append(Character.toUpperCase(str.charAt(0)));
                if (str.length() > 1) {
                    sb.append(str.substring(1));
                }
            }
        }

        return sb.toString();
    }

    public static String convertToSnakeCase(String string) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (Character.isUpperCase(string.charAt(i)) && i > 0) {
                sb.append('_');
            }

            sb.append(Character.toLowerCase(string.charAt(i)));
        }

        return sb.toString();
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static short floatToShort(float x) {
        if (x < Short.MIN_VALUE) {
            return Short.MIN_VALUE;
        }
        if (x > Short.MAX_VALUE) {
            return Short.MAX_VALUE;
        }
        return (short) Math.round(x);
    }

    public static Date nowDate() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime nowLocalDateTime() {
        return LocalDateTime.now(ZoneId.systemDefault());
    }

    public static long getTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static boolean isIpv4(String ipv4) {
        return ipv4.matches("^(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))$");
    }

    public static boolean isIpv4Mask(String ipv4Mask) {
        String[] ip = ipv4Mask.split("/");
        if (ip.length != 2) {
            return false;
        }

        if (!isIpv4(ip[0])) {
            return false;
        }

        int mask = Integer.parseInt(ip[1]);
        return mask >= 1 && mask <= 128;
    }

    public static boolean isIpv6(String ipv6) {
        return ipv6.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$|^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:)(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$");
    }

    public static boolean isIpv6Mask(String ipv6Mask) {
        String[] ip = ipv6Mask.split("/");
        if (ip.length != 2) {
            return false;
        }

        if (!isIpv6(ip[0])) {
            return false;
        }


        int mask = Integer.parseInt(ip[1]);
        return mask >= 1 && mask <= 128;
    }

    public static boolean isIp(String ip) {
        return (isIpv4(ip) || isIpv6(ip));
    }

    public static boolean isIpMask(String ipMask) {
        return (isIpv4Mask(ipMask) || isIpv6Mask(ipMask));
    }

    public static boolean isMac(String mac) {
        return mac.matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
    }

    public static boolean isPortNumber(int portNumber) {
        return 0 <= portNumber && portNumber <= 65535;
    }

    public static SubnetUtils getSubnetUtils(String cidrNotation){ return new SubnetUtils(cidrNotation); }
    public static SubnetUtils getSubnetUtils(String address, String mask){ return new SubnetUtils(address, mask); }
}
