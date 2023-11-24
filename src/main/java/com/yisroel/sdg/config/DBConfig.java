package com.yisroel.sdg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "sdg")
public class DBConfig {
    private String defaultHost;
    private String defaultDb;
    private String defaultUser;
    private String defaultPs;
}
