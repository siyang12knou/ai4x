package com.kailoslab.ai4x.event.stomp;

import com.kailoslab.ai4x.utils.Constants;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEvent;

@ConfigurationProperties(prefix = "ai4x.stomp")
public class StompProperties {

    private String endPoint;
    private String appPrefix;
    private String topicPrefix;

    private String brokerUri;
    private String clientTimeout;
    private int reconnTimeToBroker;

    @PostConstruct
    public void init() {
        if (endPoint == null) {
            endPoint = "/stomp";
        }

        if (appPrefix == null) {
            appPrefix = "/app";
        }

        if (topicPrefix == null) {
            topicPrefix = "/topic";
        }

        if (brokerUri == null) {
            brokerUri = "";
        }

        if (clientTimeout == null) {
            clientTimeout = "60000";
        }

        if (reconnTimeToBroker < 1) {
            reconnTimeToBroker = 6;
        }
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getAppPrefix() {
        return appPrefix;
    }

    public void setAppPrefix(String appPrefix) {
        this.appPrefix = appPrefix;
    }

    public String getTopicPrefix() {
        return topicPrefix;
    }

    public void setTopicPrefix(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }

    public String getBrokerUri() {
        return brokerUri;
    }

    public void setBrokerUri(String brokerUri) {
        this.brokerUri = brokerUri;
    }

    public String getClientTimeout() {
        return clientTimeout;
    }

    public void setClientTimeout(String clientTimeout) {
        this.clientTimeout = clientTimeout;
    }

    public int getReconnTimeToBroker() {
        return reconnTimeToBroker;
    }

    public void setReconnTimeToBroker(int reconnTimeToBroker) {
        this.reconnTimeToBroker = reconnTimeToBroker;
    }

    public String getTopicDestinationForApplicationEvent(Class<? extends ApplicationEvent> eventClass) {
        return getTopicDestinationForApplicationEvent(eventClass.getName());
    }

    public String getTopicDestinationForApplicationEvent(String eventClassName) {
        return topicPrefix + Constants.broadcastTopic + Constants.SLASH +
                StringUtils.replace(eventClassName, Constants.DOT, Constants.SLASH);
    }

    public String getAppDestinationForApplicationEvent(Class<? extends ApplicationEvent> eventClass) {
        return getAppDestinationForApplicationEvent(eventClass.getName());
    }

    public String getAppDestinationForApplicationEvent(String eventClassName) {
        return appPrefix + Constants.broadcastTopic + Constants.SLASH +
                StringUtils.replace(eventClassName, Constants.DOT, Constants.SLASH);
    }

    public String removeTopicPrefix(String topic) {
        if(topic.startsWith(topicPrefix)) {
            topic = topic.substring(topicPrefix.length());
        } else if(topic.startsWith(appPrefix)) {
            topic = topic.substring(appPrefix.length());
        }

        String broadcastPrefix = Constants.broadcastTopic + Constants.SLASH;
        if(topic.startsWith(broadcastPrefix)) {
            topic = topic.substring(broadcastPrefix.length());
        }

        return topic;
    }

    public String appendTopicBroadcastPrefix(String topic) {
        if(topic.startsWith(Constants.SLASH)) {
            topic = Constants.SLASH + topic;
        }

        if(!topic.startsWith(Constants.broadcastTopic)) {
            topic = Constants.broadcastTopic + topic;
        }

        if(!topic.startsWith(topicPrefix)) {
            topic = topicPrefix + topic;
        }

        return topic;
    }
}
