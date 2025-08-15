package com.dat.event.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieNameCustomizer() {
        return factory -> factory.addContextCustomizers(context -> {
            context.setSessionCookieName("JSESSIONID_");
            context.setCookies(true);
        });
    }

}
