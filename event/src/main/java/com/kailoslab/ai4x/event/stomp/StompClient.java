package com.kailoslab.ai4x.event.stomp;

import com.kailoslab.ai4x.utils.Constants;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class StompClient extends StompSessionHandlerAdapter {

    private final ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();

    private String brokerUri;
    private final String timeout;
    private final int reconTime;
    private final RestTemplate restTemplate;
    private final StompProperties properties;

    private final AtomicBoolean connecting = new AtomicBoolean(false);
    private final AtomicReference<StompSession> stompSession = new AtomicReference<>();
    private volatile ScheduledFuture<?> reconnectFuture;
    private final List<Subscriber> subscribers = Collections.synchronizedList(new ArrayList<>());

    public StompClient(RestTemplate restTemplate, StompProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.brokerUri = properties.getBrokerUri();
        this.timeout = properties.getClientTimeout();
        this.reconTime = properties.getReconnTimeToBroker();
        checkBrokerUri(properties.getEndPoint());
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        connectToBroker();
    }

    private void checkBrokerUri(String endPoint) {
        if (!this.brokerUri.endsWith(endPoint)) {
            this.brokerUri = this.brokerUri + endPoint;
        }
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        stompSession.set(session);
        subscribeAll();
        log.info("Connected a broker: " + brokerUri);
        connecting.set(false);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        log.info("Cannot connect a broker: " + brokerUri, exception);
        reconnect();
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        if (exception instanceof ConnectionLostException || // Disconnected
                (
                        exception instanceof ResourceAccessException &&
                                exception.getCause() != null && exception.getCause() instanceof ConnectException
                )
        ) {
            log.error("Disconnected a broker: " + brokerUri, exception);
            reconnect();
        }
    }

    @PreDestroy
    public void destroy() {
        clearSubscriptions();
        scheduled.shutdown();
    }

    public StompProperties getStompProperties() {
        return properties;
    }

    public void send(StompHeaders headers, Object payload) {
        if (stompSession.get() != null) {
            StompSession session = stompSession.get();
            session.send(headers, payload);
        }
    }

    public void send(String destination, Object payload) {
        if (stompSession.get() != null) {
            StompSession session = stompSession.get();
            synchronized (session) {
                session.send(destination, payload);
            }
        }
    }

    @SafeVarargs
    public final List<Subscriber> subscribeApplicationEvent(StompFrameHandler handler, Class<? extends ApplicationEvent>... eventClass) {
        List<Subscriber> result = new ArrayList<>(eventClass.length);
        for (Class<? extends ApplicationEvent> clazz : eventClass) {
            StompHeaders headers = new StompHeaders();
            String destination = properties.getTopicPrefix() + Constants.broadcastTopic + Constants.SLASH +
                    StringUtils.replace(clazz.getName(), Constants.DOT, Constants.SLASH);
            headers.setDestination(destination);
            result.add(subscribe(headers, handler));
        }

        return result;
    }

    public Subscriber subscribe(String destination, StompFrameHandler handler) {
        StompHeaders headers = new StompHeaders();
        headers.setDestination(destination);
        return subscribe(headers, handler);
    }

    public Subscriber subscribe(StompHeaders headers, StompFrameHandler handler) {
        String destination = headers.getDestination();
        if (StringUtils.isNotEmpty(destination) && destination.contains(Constants.DOT)) {
            destination = StringUtils.replace(destination, Constants.DOT, Constants.SLASH);
            headers.setDestination(destination);
        }

        Subscriber subscriber = new Subscriber(headers, handler);
        int index = subscribers.indexOf(subscriber);
        if (index > -1) {
            subscriber = subscribers.get(index);
        } else {
            subscribers.add(subscriber);
        }

        if (stompSession.get() != null) {
            subscriber.setSubscription(subscribe(subscriber));
        }

        return subscriber;
    }

    public void connectToBroker() {
        if (StringUtils.isNotEmpty(brokerUri) && !connecting.get()) {
            connecting.set(true);
            stompSession.set(null);
            clearSubscriptions();

            StandardWebSocketClient standardWs = new StandardWebSocketClient();
            standardWs.getUserProperties().put(org.apache.tomcat.websocket.Constants.IO_TIMEOUT_MS_PROPERTY, timeout);
            List<Transport> transports = new ArrayList<>(2);
            transports.add(new WebSocketTransport(standardWs));
            if (restTemplate != null) {
                RestTemplateXhrTransport restTemplateTransport = new RestTemplateXhrTransport(restTemplate);
                transports.add(restTemplateTransport);
            }
            WebSocketClient transport = new SockJsClient(transports);
            WebSocketStompClient stompClient = new WebSocketStompClient(transport);
            MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
            converter.setObjectMapper(Constants.JSON_MAPPER);
            stompClient.setMessageConverter(converter);

            CompletableFuture<StompSession> sessionFuture = stompClient.connectAsync(brokerUri, this);
            log.info("Connecting a broker: " + brokerUri);
        }
    }

    private void reconnect() {
        if (connecting.get()) {
            connecting.set(false);
        }

        if (reconnectFuture == null) {
            reconnectFuture = scheduled.scheduleWithFixedDelay(() -> {
                boolean interrupted = false;
                try {
                    // 연결 성공했을 경우 재연결 시도 종료
                    if (stompSession.get() == null) {
                        if (reconnectFuture == null) { // 혹시 몰라 인터럽트 거는 코드 포함시킴
                            while (reconnectFuture == null) {
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    interrupted = true;
                                }
                            }
                        } else {
                            reconnectFuture.cancel(false);
                            reconnectFuture = null;
                        }
                    } else {
                        connectToBroker();
                    }
                } finally {
                    if (interrupted) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, reconTime, reconTime, TimeUnit.SECONDS);
        }
    }

    private void subscribeAll() {
        if (stompSession.get() != null) {
            for (Subscriber subscriber : subscribers) {
                subscriber.setSubscription(stompSession.get().subscribe(subscriber.getHeaders(), subscriber.getHandler()));
            }
        }
    }

    private Subscription subscribe(Subscriber subscriber) {
        return stompSession.get().subscribe(subscriber.getHeaders(), subscriber.getHandler());
    }

    private void clearSubscriptions() {
        if (subscribers.size() > 0) {
            for (Subscriber subscriber : subscribers) {
                subscriber.unsubscribe();
            }
        }
    }

    class Subscriber {
        private final StompHeaders headers;
        private final StompFrameHandler handler;
        private Subscription subscription;

        public Subscriber(StompHeaders headers, StompFrameHandler handler) {
            super();
            this.headers = headers;
            this.handler = handler;
        }

        public StompHeaders getHeaders() {
            return headers;
        }

        public StompFrameHandler getHandler() {
            return handler;
        }

        public void unsubscribe() {
            if (subscription != null) {
                subscription.unsubscribe();
            }

            subscription = null;
        }

        public void unsubscribe(StompHeaders headers) {
            if (subscription != null) {
                subscription.unsubscribe(headers);
            }

            subscription = null;
        }

        private void setSubscription(Subscription subscription) {
            this.subscription = subscription;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getEnclosingInstance().hashCode();
            result = prime * result + ((handler == null) ? 0 : handler.hashCode());
            result = prime * result + ((headers == null) ? 0 : headers.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Subscriber other = (Subscriber) obj;
            if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
                return false;
            if (handler == null) {
                if (other.handler != null)
                    return false;
            } else if (!handler.equals(other.handler))
                return false;
            if (headers == null) {
                return other.headers == null;
            } else return headers.equals(other.headers);
        }

        private StompClient getEnclosingInstance() {
            return StompClient.this;
        }

    }
}
