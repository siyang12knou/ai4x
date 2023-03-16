package com.kailoslab.ai4x.event.stomp;

import com.kailoslab.ai4x.event.BroadcastEvent;

public interface StompBroadcastEventListener<T extends BroadcastEvent> {
    void listen(T event);
}
