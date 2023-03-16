package com.kailoslab.ai4x.event.stomp;

import com.kailoslab.ai4x.utils.Ai4xUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * STOMP를 활성화 시키고 싶을 때 사용할 설정 클래스로 만약 활성화 시키려면 StompConfig 클래스를 상속받은 후 @Configuration, @EnableWebSocketMessageBroker 애노테이션을 클래스에 선언하면 된다
 *
 * @author simon.yang
 */
@Slf4j
@RequiredArgsConstructor
public abstract class StompBrokerConfig implements WebSocketMessageBrokerConfigurer {
    private final StompProperties stompProperties;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes(stompProperties.getAppPrefix());
        registry.enableSimpleBroker(stompProperties.getTopicPrefix());
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(2 * 1024 * 1024);
        registry.setSendBufferSizeLimit(2 * 1024 * 1024);
        registry.setSendTimeLimit(20000);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(stompProperties.getEndPoint());
        registry.addEndpoint(stompProperties.getEndPoint()).withSockJS();
        log.info("Enabled a STOMP Broker on SockJS: " + Ai4xUtils.getOutboundIp() + ", " + stompProperties.getEndPoint());
    }
}
