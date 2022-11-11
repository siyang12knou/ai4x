package com.kailoslab.ai4x.collector;

import com.kailoslab.ai4x.commons.annotation.Help;
import com.kailoslab.ai4x.commons.annotation.Title;
import com.kailoslab.ai4x.batch.component.BatchService;
import com.kailoslab.ai4x.batch.data.entity.BatchJobInfo;
import com.kailoslab.ai4x.batch.exception.BatchException;
import com.kailoslab.ai4x.collector.annotation.Close;
import com.kailoslab.ai4x.collector.annotation.Collector;
import com.kailoslab.ai4x.collector.annotation.Execute;
import com.kailoslab.ai4x.commons.service.PropertiesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@Slf4j
@Component
public class CollectorService implements ApplicationListener<ApplicationStartedEvent> {

    public static final String JOB_GROUP = "collector";

    private static CollectorService instance;
    public static CollectorService getInstance() {
        return instance;
    }

    private Map<String, CollectorWrapper> collectors;
    private Map<String, Future<?>> futures;

    private final BatchService batchService;
    private final Executor executor;
    private final PropertiesService propertiesService;

    public CollectorService(BatchService batchService, Executor executor, PropertiesService propertiesService) {
        this.batchService = batchService;
        this.executor = executor;
        this.propertiesService = propertiesService;
        instance = this;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        Map<String, Object> collectors = event.getApplicationContext().getBeansWithAnnotation(Collector.class);
        this.collectors = Collections.synchronizedMap(new HashMap<>(collectors.size()));
        collectors.forEach(this::parseCollectorInfo);
        this.futures = Collections.synchronizedMap(new HashMap<>(collectors.size()));
    }

    private void parseCollectorInfo(String name, Object collectorBean) {
        Method executeMethod = findExecuteMethod(collectorBean);
        if(executeMethod != null) {
            CollectorWrapper collectorWrapper = new CollectorWrapper();
            Collector collectorAnnotation = collectorBean.getClass().getAnnotation(Collector.class);
            collectorWrapper.setName(StringUtils.isEmpty(collectorAnnotation.value()) ? name : collectorAnnotation.value());
            Title collectorTitle = collectorBean.getClass().getAnnotation(Title.class);
            collectorWrapper.setTitle(ObjectUtils.isEmpty(collectorTitle) || StringUtils.isEmpty(collectorTitle.value()) ? collectorWrapper.getName() : collectorTitle.value());
            Help collectorHelp = collectorBean.getClass().getAnnotation(Help.class);
            collectorWrapper.setHelp(ObjectUtils.isEmpty(collectorHelp) ? null : collectorHelp.value());
            collectorWrapper.setExecuteMethod(executeMethod);
            collectorWrapper.setCloseMethod(findCloseMethod(collectorBean));
            collectorWrapper.setCollector(collectorBean);
            propertiesService.saveProperties(JOB_GROUP, collectorWrapper.getName(), executeMethod.getParameters());
            this.collectors.put(collectorWrapper.getName(), collectorWrapper);
        }
    }

    public void saveProperties(String collectorName, Object... properties) throws CollectorException {
        CollectorWrapper collectorWrapper = collectors.get(collectorName);
        if(collectorWrapper != null) {
            Method executeMethod = collectorWrapper.getExecuteMethod();
            Parameter[] parameters = executeMethod.getParameters();
            if(parameters.length != properties.length) {
                throw new CollectorException("Not equals length of properties and parameters of the execute method.");
            }

            Map<String, Object> propertiesMap = new LinkedHashMap<>(properties.length);
            int i = 0;
            for (Parameter parameter :
                    parameters) {
                propertiesMap.put(parameter.getName(), properties[i]);
            }

            saveProperties(collectorName, propertiesMap);
        } else {
            throw new CollectorException(String.format("Cannot find a collector [%s].", collectorName));
        }
    }

    public void saveProperties(String collectorName, Map<String, Object> properties) throws CollectorException {
        CollectorWrapper collectorWrapper = collectors.get(collectorName);
        if(collectorWrapper != null) {
            String serviceName = JOB_GROUP;
            String instanceName = collectorName;
            Map<String, Object> saveProperties;
            if(properties instanceof LinkedHashMap) {
               saveProperties = properties;
            } else {
                saveProperties = new LinkedHashMap<>(properties.size());
                Method executeMethod = collectorWrapper.getExecuteMethod();
                Parameter[] parameters = executeMethod.getParameters();
                for (Parameter parameter :
                        parameters) {
                    saveProperties.put(parameter.getName(), properties.get(parameter.getName()));
                }
            }

            propertiesService.saveProperties(serviceName, instanceName, saveProperties);
        } else {
            throw new CollectorException(String.format("Cannot find a collector [%s].", collectorName));
        }
    }

    public void start(String collectorName, Object... properties) throws CollectorException {
        CollectorWrapper collectorWrapper = collectors.get(collectorName);
        if(collectorWrapper != null) {
            if(isStarted(collectorName)) {
                throw new CollectorException("Already start the %s for %s".formatted(
                        (batchService.isExist(JOB_GROUP, collectorName) ? "batch of collector" : "collector"),
                        collectorName));
            }

            Object[] params;
            if(ObjectUtils.isEmpty(properties)) {
                Map<String, Object> propertiesMap = propertiesService.getProperties(JOB_GROUP, collectorName);
                params = propertiesMap.values().toArray(new Object[propertiesMap.size()]);
            } else {
                params = properties;
            }

            collectorWrapper.setStarted(true);
            Future<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    collectorWrapper.execute(params);
                    return true;
                } catch (CollectorException e) {
                    log.error("Failure execute a collector", e);
                    return false;
                }
            }, executor).whenComplete((result, e) -> {
                collectorWrapper.setStarted(false);
                if(e != null) {
                    log.error("Failure execute a collector", e);
                }

                try {
                    collectorWrapper.close();
                } catch (CollectorException ex) {
                    log.error("Failure close a collector.", ex);
                } finally {
                    futures.remove(collectorName);
                }
            });

            futures.put(collectorName, future);
        } else {
            throw new CollectorException(String.format("Cannot find a collector [%s].", collectorName));
        }
    }

    public void registerBatch(String collectorName, Object... properties) throws CollectorException {
        CollectorWrapper collectorWrapper = collectors.get(collectorName);
        if(collectorWrapper != null) {
            if(isStarted(collectorName)) {
                stop(collectorName);
            }

            BatchJobInfo jobInfo = new BatchJobInfo(JOB_GROUP, collectorName, CollectorJob.class.getName());

            Map<String, Object> jobDataMap = new HashMap<>(2);
            jobDataMap.put(CollectorJob.KEY_COLLECTOR_NAME, collectorName);
            jobDataMap.put(CollectorJob.KEY_COLLECTOR_PROPERTIES, properties);
            try {
                batchService.start(jobInfo, jobDataMap);
            } catch (BatchException e) {
                throw new CollectorException(String.format("Cannot start a collector [%s].", collectorName));
            }
            collectorWrapper.setStarted(true);
        } else {
            throw new CollectorException(String.format("Cannot find a collector [%s].", collectorName));
        }
    }

    public void stop(String collectorName) throws CollectorException {
        CollectorWrapper collectorWrapper = collectors.get(collectorName);
        if(collectorWrapper != null) {
            if (isStarted(collectorName)) {
                if(futures.containsKey(collectorName)) {
                    Future future = futures.get(collectorName);
                    future.cancel(true);
                    while(!future.isDone()) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            new CollectorException("An interrupt occurred while stopping a collector [%s].".formatted(collectorName), e);
                        }
                    }
                    futures.remove(collectorName);
                } else {
                    if (batchService.delete(JOB_GROUP, collectorName)) {
                        collectorWrapper.close();
                    } else {
                        throw new CollectorException(String.format("Cannot stop a collector [%s].", collectorName));
                    }
                }
            } else {
                throw new CollectorException(String.format("Cannot find a started collector [%s].", collectorName));
            }
        } else {
            throw new CollectorException(String.format("Cannot find a collector [%s].", collectorName));
        }
    }

    private boolean isStarted(String collectName) {
        return batchService.isExist(JOB_GROUP, collectName) || futures.containsKey(collectName);
    }

    private Method findExecuteMethod(Object collector) {
        return findMethod(collector, Execute.class, "start");
    }

    private Method findCloseMethod(Object collector) {
        return findMethod(collector, Close.class, "stop");
    }

    private Method findMethod(Object collector, Class<? extends Annotation> annotation, String defaultMethodName) {
        Object collectorObject;
        if(collector instanceof String) {
            collectorObject = collectors.get(collector.toString());
        } else {
            collectorObject = collector;
        }

        Method[] methods = collectorObject.getClass().getDeclaredMethods();
        for (Method method :
                methods) {
            if (method.isAnnotationPresent(annotation)) {
                return method;
            }
        }

        for (Method method :
                methods) {
            if (method.getName().equals(defaultMethodName)) {
                return method;
            }
        }

        return null;
    }
}
