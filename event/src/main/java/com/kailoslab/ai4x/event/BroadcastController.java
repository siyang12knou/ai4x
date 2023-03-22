package com.kailoslab.ai4x.event;

import com.kailoslab.ai4x.event.stomp.StompProperties;
import com.kailoslab.ai4x.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class BroadcastController {

    private final StompProperties stompProperties;
    private final SimpMessagingTemplate template;
    private final ApplicationEventPublisher eventPublisher;

    public BroadcastController(@Autowired StompProperties stompProperties,
                               @Autowired(required = false) SimpMessagingTemplate template,
                               @Autowired ApplicationEventPublisher eventPublisher) {
        this.stompProperties = stompProperties;
        this.template = template;
        this.eventPublisher = eventPublisher;
    }

    @MessageMapping(Constants.broadcastTopic + "/**")
    public void broadcastByStompClient(@DestinationVariable String destination, String payload) {
        try {
            BroadcastEvent<?> event = BroadcastEvent.create(destination, payload, stompProperties);
            eventPublisher.publishEvent(event); // 내부
            send(destination, payload); // 외부
        } catch (Throwable ex) {
            log.error("Cannot parse a BroadcastEvent: {} , {}", destination, payload);
        }
    }

    private void send(String destination, String payload) {
        if (template != null) {
            template.convertAndSend(stompProperties.appendTopicBroadcastPrefix(destination), payload);
        }
    }
}
