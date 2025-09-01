package com.dat.event.email.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.mail")
@Data
public class EmailProperties {

    private List<String> recipients;
    private String templatePath;

}
