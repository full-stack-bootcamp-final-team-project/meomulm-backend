package com.meomulm.common.config;

import com.meomulm.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collections;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. 웹 브라우저용 (SockJS)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(
                        "http://localhost:*",
                        "http://10.0.2.2:*",
                        "https://*.onrender.com",
                        "https://meomulm.com",
                        "https://www.meomulm.com"
                )
                .withSockJS();

        // 2. 네이티브 WebSocket용 (Flutter, React Native 등)
        registry.addEndpoint("/ws-native")
                .setAllowedOriginPatterns(
                        "http://localhost:*",
                        "http://10.0.2.2:*",
                        "https://*.onrender.com",
                        "https://meomulm.com",
                        "https://www.meomulm.com"
                );
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    log.info("🔌 WebSocket 연결 시도");

                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);

                        try {
                            if (jwtUtil.validateToken(token)) {
                                int userId = jwtUtil.getUserIdFromToken(token);

                                Authentication auth = new UsernamePasswordAuthenticationToken(
                                        String.valueOf(userId),
                                        null,
                                        Collections.emptyList()
                                );

                                accessor.setUser(auth);
                                log.info("✅ WebSocket 인증 성공 - userId: {}", userId);
                            } else {
                                log.warn("⚠️ 유효하지 않은 토큰");
                                throw new MessageDeliveryException("유효하지 않은 토큰입니다.");
                            }
                        } catch (Exception e) {
                            log.error("❌ WebSocket 인증 실패: {}", e.getMessage());
                            throw new MessageDeliveryException("인증에 실패했습니다.");
                        }
                    } else {
                        log.warn("⚠️ Authorization 헤더 없음");
                        // 개발 중 인증 없이 연결 허용 (선택사항)
                        // throw new MessageDeliveryException("인증 토큰이 필요합니다.");
                    }
                }
                return message;
            }
        });
    }
}
