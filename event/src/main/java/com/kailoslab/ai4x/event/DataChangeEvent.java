package com.kailoslab.ai4x.event;

public class DataChangeEvent extends BroadcastEvent<DataChangeEventSource> {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public DataChangeEvent(DataChangeEventSource source) {
        super(source);
    }
}
