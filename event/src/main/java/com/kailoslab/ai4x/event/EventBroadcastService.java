package com.kailoslab.ai4x.event;

import com.kailoslab.ai4x.event.stomp.StompClient;
import com.kailoslab.ai4x.event.stomp.StompProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Service
public class EventBroadcastService {

    private final StompProperties stompProperties;
    private final SimpMessagingTemplate messageTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final Executor async;
    private final StompClient stompClient;

    public EventBroadcastService(@Autowired(required = false) StompProperties stompProperties,
                                 @Autowired(required = false) SimpMessagingTemplate messageTemplate,
                                 @Autowired(required = false) StompClient stompClient,
                                 @Autowired ApplicationEventPublisher eventPublisher,
                                 @Autowired Executor async) {
        this.stompProperties = stompProperties;
        this.messageTemplate = messageTemplate;
        this.stompClient = stompClient;
        this.eventPublisher = eventPublisher;
        this.async = async;
    }

    public void broadcast(ApplicationEvent event) {
        async.execute(() -> broadcastToInternal(event));
        async.execute(() -> broadcastToExternal(event));
    }

    public void broadcastToInternal(ApplicationEvent event) {
        if (eventPublisher != null) {
            eventPublisher.publishEvent(event);
        }
    }

    public void broadcastToExternal(ApplicationEvent event) {
        if (messageTemplate != null) {
            messageTemplate.convertAndSend(stompProperties.getTopicDestinationForApplicationEvent(event.getClass()), getPayload(event));
        } else if (stompClient != null) {
            stompClient.send(stompProperties.getTopicDestinationForApplicationEvent(event.getClass()), getPayload(event));
        }
    }

    public void broadcastToExternal(String eventClassName, Object payload) {
        if (messageTemplate != null) {
            messageTemplate.convertAndSend(stompProperties.getTopicDestinationForApplicationEvent(eventClassName), payload);
        } else if (stompClient != null) {
            stompClient.send(stompProperties.getTopicDestinationForApplicationEvent(eventClassName), payload);
        }
    }

    private Object getPayload(ApplicationEvent event) {
        if(event instanceof BroadcastEvent) {
            return ((BroadcastEvent) event).getEventSource();
        } else {
            return event;
        }
    }

    public StompClient getStompClient() {
        return stompClient;
    }

    public boolean isOnlyStompClient() {
        return messageTemplate == null && stompClient != null;
    }
}
