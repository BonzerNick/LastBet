package com.example.casino.listener;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class WebSocketEventListener {

    // Событие подключения клиента
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Логируем подключение
        log.info("Клиент подключился: sessionId={}, headers={}",
                headerAccessor.getSessionId(),
                headerAccessor.getNativeHeader("login"));
    }

    // Событие отключения клиента
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Логируем отключение
        log.info("Клиент отключился: sessionId={}", headerAccessor.getSessionId());
    }
}