package com.kailoslab.ai4x.collector;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@NoArgsConstructor
@Setter
@Getter
public class CollectorWrapper implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String title;
    private String titleKey;
    private String help;
    private boolean started = false;
    private String jobId;
    transient private Object collector;
    transient private Method executeMethod;
    transient private Method closeMethod;

    public void execute(Object... properties) throws CollectorException {
        if(collector != null && executeMethod != null) {
            try {
                executeMethod.invoke(collector, properties);
            } catch (Throwable e) {
                throw new CollectorException(String.format("Cannot execute a collector [%s].", name), e);
            }
        } else {
            throw new CollectorException(String.format("Cannot find a collector [%s].", name));
        }
    }

    public void close() throws CollectorException {
        setJobId(null);
        if(collector != null && closeMethod != null) {
            try {
                closeMethod.invoke(collector);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new CollectorException(String.format("Cannot close a collector [%s].", name), e);
            }
        }
    }
}
