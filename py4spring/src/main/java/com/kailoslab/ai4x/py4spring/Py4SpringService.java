package com.kailoslab.ai4x.py4spring;

import com.kailoslab.ai4x.py4spring.controller.Py4SpringDispatcher;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import py4j.GatewayServer;
import py4j.GatewayServerListener;
import py4j.Py4JNetworkException;
import py4j.Py4JServerConnection;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.reflections.scanners.Scanners.TypesAnnotated;

@Slf4j
public class Py4SpringService {

    private final ApplicationContext applicationContext;
    @Getter
    private final Py4SpringContext context;
    @Getter
    private final Py4SpringDispatcher dispatcher;
    @Getter
    private final Py4Utils utils;
    private final Map<String, PythonBeanInterceptor> interceptorMap;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private IPythonContext pythonContext;
    private GatewayServer gatewayServer;

    public Py4SpringService(ApplicationContext applicationContext, Py4SpringDispatcher dispatcher, Py4SpringProperties properties) {
        this.applicationContext = applicationContext;
        this.dispatcher = dispatcher;
        this.context = new Py4SpringContext();
        this.utils = new Py4Utils();
        this.interceptorMap = Collections.synchronizedMap(new HashMap<>());
        initPythonBeans();
        initGateway(properties);
    }

    private void initPythonBeans() {
        if(applicationContext instanceof ConfigurableApplicationContext configurableApplicationContext) {
            List<String> scanPackages = getScanPackages(applicationContext);
            scanPackages.forEach(scanPackage -> {
                Reflections reflections = new Reflections(scanPackage, TypesAnnotated);
                Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(PythonBean.class);
                classSet.forEach(clazz -> {
                    if(!interceptorMap.containsKey(clazz.getName())) {
                        PythonBean pythonBean = clazz.getDeclaredAnnotation(PythonBean.class);
                        String qualifier = pythonBean.value();
                        if (StringUtils.isEmpty(qualifier)) {
                            qualifier = convertClassNameToQualifier(clazz.getSimpleName());
                        }

                        Enhancer enhancer = new Enhancer();
                        enhancer.setSuperclass(clazz);
                        PythonBeanInterceptor interceptor = new PythonBeanInterceptor(clazz.getName());
                        enhancer.setCallback(interceptor);
                        Object bean = enhancer.create();
                        configurableApplicationContext.getBeanFactory().registerSingleton(qualifier, bean);
                        interceptorMap.put(clazz.getName(), interceptor);
                    }
                });
            });
        }
    }

    private void initGateway(Py4SpringProperties properties) {
        InetAddress javaAddress;
        InetAddress pythonAddress;
        try {
            javaAddress = InetAddress.getByName(properties.getSpringAddressName());
            pythonAddress = InetAddress.getByName(properties.getPythonAddressName());
        } catch(UnknownHostException e) {
            throw new Py4JNetworkException(e);
        }

        gatewayServer = new GatewayServer.GatewayServerBuilder()
                .entryPoint(this)
                .javaAddress(javaAddress).javaPort(properties.getSpringPort())
                .callbackClient(properties.getPythonPort(), pythonAddress)
                .authToken(properties.getAuthToken())
                .build();

        gatewayServer.addListener(new Py4SpringGatewayServerListener());
        gatewayServer.start();
        log.info("Started a gateway server for Py4SpringService.");
        registerPythonContext0((IPythonContext) gatewayServer.getPythonServerEntryPoint(new Class[] {IPythonContext.class}));
    }

    private void registerPythonContext0(IPythonContext pythonContext) {
        if(pythonContext != null && applicationContext instanceof ConfigurableApplicationContext configurableApplicationContext) {
            this.pythonContext = pythonContext;
            executor.execute(() -> {
                configurableApplicationContext.getBeanFactory().registerSingleton(pythonContext.getQualifier(), pythonContext);
                pythonContext.setConnected(true);
            });
        }
    }

    public List<String> getScanPackages(ApplicationContext applicationContext) {
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

    private String convertClassNameToQualifier(String className) {
        String qualifier = className;
        if (qualifier.length() > 2 &&
                StringUtils.isAllUpperCase(qualifier.substring(0, 2)) &&
                qualifier.startsWith("I")) {
            qualifier = qualifier.substring(1);
        }

        return Character.toLowerCase(qualifier.charAt(0)) + (qualifier.length() > 1 ? qualifier.substring(1) : "");
    }

    @PreDestroy
    public void shutdown() {
        if(pythonContext != null) {
            if(applicationContext instanceof ConfigurableApplicationContext configurableApplicationContext) {
                ((BeanDefinitionRegistry) configurableApplicationContext.getBeanFactory()).removeBeanDefinition(pythonContext.getQualifier());
            }
            pythonContext.setConnected(false);
            pythonContext = null;
        }

        if(gatewayServer != null) {
            gatewayServer.shutdown();
            gatewayServer = null;
        }
    }

    class Py4SpringContext implements IPy4SpringContext {

        @Override
        public void registerPythonContext(IPythonContext pythonContext) {
            registerPythonContext0(pythonContext);
        }

        @Override
        public Object getBean(String className) {
            try {
                return applicationContext.getBean(className);
            } catch (BeansException e) {
                log.error("Cannot find a bean: " + className);
                return null;
            }
        }

        @Override
        public Object getBeanOfType(String className) {
            try {
                Class<?> clazz = Class.forName(className);
                return applicationContext.getBean(clazz);
            } catch (ClassNotFoundException | BeansException e) {
                log.error("Cannot find a bean: " + className);
                return null;
            }
        }

        @Override
        public Object getBean(String className, String qualifier) {
            try {
                Class<?> clazz = Class.forName(className);
                return applicationContext.getBean(qualifier, clazz);
            } catch (ClassNotFoundException | BeansException e) {
                log.error("Cannot find a bean: " + className);
                return null;
            }
        }

        @Override
        public List<String> registerBean(IPythonBeanWrapper beanWrapper) {
            final List<String> classNames = beanWrapper.getClassNames();
            Future<List<String>> result = executor.submit(() -> {
                List<String> failedClassNames = new ArrayList<>(classNames.size());
                classNames.forEach(className -> {
                    PythonBeanInterceptor interceptor = interceptorMap.get(className);
                    try {
                        if (interceptor == null || !StringUtils.equals(interceptor.getClassName(), className)) {
                            log.info("Cannot assign a bean({}) in interceptor.", className);
                            failedClassNames.add(className);
                        }

                        assert interceptor != null;
                        interceptor.setTarget(beanWrapper.getBean());
                        log.info("Assigned a bean({}) in interceptor.", className);
                    } catch (Throwable e) {
                        log.error("Failed assign a bean({}) in interceptor.", className, e);
                        failedClassNames.add(className);
                    }
                });
                return failedClassNames;
            });

            try {
                do {
                    Thread.sleep(10);
                    log.info("{}", (result.isDone() || result.isCancelled()));
                } while (!result.isDone() && !result.isCancelled());

                if(result.isCancelled()) {
                    return classNames;
                } else {
                    return result.get();
                }
            } catch (InterruptedException | ExecutionException e) {
                return classNames;
            }
        }

        @Override
        public void unregisterBean(List<String> classNames) {
            classNames.forEach(className -> {
                PythonBeanInterceptor interceptor = interceptorMap.get(className);
                if(interceptor != null) {
                    interceptor.setTarget(null);
                }
            });
        }
    }

    class Py4SpringGatewayServerListener implements GatewayServerListener {

        @Override
        public void connectionError(Exception e) {

        }

        @Override
        public void connectionStarted(Py4JServerConnection gatewayConnection) {

        }

        @Override
        public void connectionStopped(Py4JServerConnection gatewayConnection) {

        }

        @Override
        public void serverError(Exception e) {

        }

        @Override
        public void serverPostShutdown() {

        }

        @Override
        public void serverPreShutdown() {

        }

        @Override
        public void serverStarted() {

        }

        @Override
        public void serverStopped() {

        }
    }
}
