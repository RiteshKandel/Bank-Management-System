package com.ritesh.bankmanagmentsystem.config;

import com.ritesh.bankmanagmentsystem.security.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@EnableConfigurationProperties(JwtProperties.class)
public class JpaConfig {
}

