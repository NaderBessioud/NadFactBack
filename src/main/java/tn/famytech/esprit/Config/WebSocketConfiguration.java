package tn.famytech.esprit.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketMessageBrokerConfigurer.class);

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
    	  config.enableSimpleBroker("/topic");
    	    config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
       	registry.addEndpoint("/nadfactmobile").setAllowedOriginPatterns("*"); 
    
    	registry.addEndpoint("/gs-guide-websocket").setAllowedOriginPatterns("*").withSockJS(); 
    }
}


