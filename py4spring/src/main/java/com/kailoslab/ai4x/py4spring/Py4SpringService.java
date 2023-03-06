package com.kailoslab.ai4x.py4spring;

import com.kailoslab.ai4x.py4spring.controller.Py4SpringDispatcher;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.ClassUtils;
import py4j.GatewayServer;
import py4j.Py4JException;
import py4j.Py4JNetworkException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.reflections.scanners.Scanners.TypesAnnotated;

@Slf4j
public class Py4SpringService implements BeanFactoryPostProcessor, ApplicationListener<ApplicationStartedEvent> {

    private final ApplicationContext applicationContext;
    @Getter
    private final Py4SpringContext context;
    @Getter
    private final Py4SpringDispatcher dispatcher;
    @Getter
    private final Py4Utils utils;
    private final Py4SpringPythonProxyRepository proxyRepository;
    private final Py4SpringProperties properties;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private IPythonContext pythonContext;
    private GatewayServer gatewayServer;

    public Py4SpringService(ApplicationContext applicationContext, Py4SpringDispatcher dispatcher, Py4SpringProperties properties, Py4SpringPythonProxyRepository proxyRepository) {
        this.applicationContext = applicationContext;
        this.dispatcher = dispatcher;
        this.context = new Py4SpringContext();
        this.utils = new Py4Utils();
        this.proxyRepository = proxyRepository;
        this.properties = properties;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        List<String> scanPackages = getScanPackages(applicationContext);
        scanPackages.forEach(scanPackage -> {
            Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(scanPackage).setScanners(TypesAnnotated));
            Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(PythonProxy.class);
            classSet.forEach(clazz -> {
                String shortName = ClassUtils.getShortNameAsProperty(clazz);
                if(!beanFactory.containsBean(shortName)) {
                    beanFactory.registerSingleton(shortName, proxyRepository.createPythonProxy(clazz));
                    log.info("register a PythonBeanInterceptor for " + clazz);
                }
            });
        });
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
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
                .build();

        gatewayServer.start();
        log.info("Started a gateway server for Py4SpringService.");
        registerPythonContext0((IPythonContext) gatewayServer.getPythonServerEntryPoint(new Class[] {IPythonContext.class}));
    }

    private void registerPythonContext0(IPythonContext pythonContext) {
        try{
            if(applicationContext instanceof ConfigurableApplicationContext configurableApplicationContext) {
                this.pythonContext = pythonContext;
                this.dispatcher.clearRestFunctionList();
                executor.execute(() -> {
                    try {
                        pythonContext.setConnected(true);
                        log.info("Connected a py4spring of python.");
                    } catch(Py4JException e) {
                        log.error("Cannot connect a py4spring of python.");
                    }
                });
            }
        } catch (Py4JException e) {
            log.info("It is not yet connected with Python.");
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

    @PreDestroy
    public void shutdown() {
        if(pythonContext != null) {
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
        public Object getBean(String qualifier) {
            try {
                return applicationContext.getBean(qualifier);
            } catch (BeansException e) {
                log.error("Cannot find a bean: " + qualifier);
                return null;
            }
        }

        @Override
        public Object getBeanByClassName(String className) {
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
        public List<String> registerBean(IPythonBeanWrapper beanWrapper, List<String> classNames) {
            Future<List<String>> result = executor.submit(() -> registerBeanSync(beanWrapper, classNames));
            try {
                do {
                    Thread.sleep(10);
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
        public List<String> registerBeanSync(IPythonBeanWrapper beanWrapper, List<String> classNames) {
            List<String> failedClassNames = new ArrayList<>();
            classNames.forEach(className -> {
                PythonBeanInterceptor interceptor = proxyRepository.getInterceptor(className);
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
        }

        @Override
        public void unregisterBean(List<String> classNames) {
            classNames.forEach(className -> {
                PythonBeanInterceptor interceptor = proxyRepository.getInterceptor(className);
                if(interceptor != null) {
                    interceptor.setTarget(null);
                }
            });
        }

        @Override
        public Map<String, ?> getSpringSystemInfo() {
            return System.getProperties().entrySet().stream().collect(
                Collectors.toMap(
                    e -> String.valueOf(e.getKey()),
                    e -> String.valueOf(e.getValue()),
                    (prev, next) -> next, HashMap::new
                ));
        }
    }
}
