package com.rutusoft.flowable.mail.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mail-reader.mail.imap")
public class ImapProperties {
    private String host;
    private Integer port;
    private String username;
    private String password;
}