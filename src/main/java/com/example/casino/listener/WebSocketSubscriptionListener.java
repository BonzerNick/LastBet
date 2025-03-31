package com.example.casino.listener;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@Slf4j
public class WebSocketSubscriptionListener {

    // Отслеживаем подписку клиента на каналы
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Получаем информацию о канале и сессии
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();

        log.info("Клиент с sessionId={} подписался на канал {}", sessionId, destination);
    }
}