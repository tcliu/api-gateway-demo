package app.api.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class WebSocketConfig {

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public HandlerMapping handlerMapping(WebSocketHandler webSocketHandler,
                                         MyWebSocketHandler myWebSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/e1", webSocketHandler);
        map.put("/e2", myWebSocketHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return session -> {
            log.info("Web socket session: {}", session);
            return session.send(sink -> sink.onNext(session.textMessage("123")));
        };
    }



}
