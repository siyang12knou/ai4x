package com.kailoslab.ai4x.event.stomp;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.kailoslab.ai4x.event.BroadcastEvent;
import com.kailoslab.ai4x.event.EventBroadcastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class StompBroadcastEventReceiver implements StompFrameHandler {
    private final Map<Class<? extends BroadcastEvent>, List<StompBroadcastEventListener>> listeners = Collections.synchronizedMap(new HashMap<>());

    private final EventBroadcastService eventBroadcastService;
    private final Executor executor;

    public StompBroadcastEventReceiver(@Autowired(required = false) StompClient stompClient,
                                       @Autowired(required = false) StompProperties stompProperties,
                                       @Autowired EventBroadcastService eventBroadcastService,
                                       @Autowired Executor executor) {
        this.eventBroadcastService = eventBroadcastService;
        this.executor = executor;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        String destination = headers.getDestination();
        return BroadcastEvent.getBroadcastEventSourceClass(destination,
                eventBroadcastService.getStompClient().getStompProperties());
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        String destination = headers.getDestination();
        try {
            BroadcastEvent event = BroadcastEvent.create(destination, payload,
                    eventBroadcastService.getStompClient().getStompProperties());
            if(eventBroadcastService.isOnlyStompClient()) {
                eventBroadcastService.broadcastToInternal(event);
            }
            List<StompBroadcastEventListener> listenersOfEvent = listeners.get(event.getClass());
            for (StompBroadcastEventListener listener: listenersOfEvent) {
                executor.execute(() -> listener.listen(event));
            }
        } catch (Throwable e) {
            log.error("Cannot handle a frame.", e);
        }
    }

    public void addStompBroadcastEventListener(StompBroadcastEventListener eventListener) {
        checkState();
        Class<? extends BroadcastEvent> eventClass = getBroadcastEventClassForStompBroadcastEventListener(eventListener);
        if(eventClass != null) {
            List<StompBroadcastEventListener> listenersOfEvent = listeners.computeIfAbsent(eventClass, k -> Collections.synchronizedList(new ArrayList<>(1)));

            if (!listenersOfEvent.contains(eventListener)) {
                eventBroadcastService.getStompClient().subscribeApplicationEvent(this, eventClass);
                listenersOfEvent.add(eventListener);
            }
        }
    }

    public void removeStompBroadcastEventListener(StompBroadcastEventListener eventListener) {
        checkState();
        Class<? extends BroadcastEvent> eventClass = getBroadcastEventClassForStompBroadcastEventListener(eventListener);
        if(eventClass != null) {
            List<StompBroadcastEventListener> listenersOfEvent = listeners.get(eventClass);
            listenersOfEvent.remove(eventListener);
        }
    }

    private Class<? extends BroadcastEvent> getBroadcastEventClassForStompBroadcastEventListener(StompBroadcastEventListener listener) {
        Type[] genericInterfaces = listener.getClass().getGenericInterfaces();
        for(Type genericInterface: genericInterfaces) {
            String interfaceName = TypeFactory.rawClass(genericInterface).getName();
            if(interfaceName.equals(StompBroadcastEventListener.class.getName())) {
                Type eventType = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
                return (Class<? extends BroadcastEvent>) TypeFactory.rawClass(eventType);
            }
        }

        return null;
    }

    private void checkState() {
        if(eventBroadcastService.getStompClient() == null) {
            throw new IllegalStateException("Cannot support a StompClient.");
        }
    }
}
