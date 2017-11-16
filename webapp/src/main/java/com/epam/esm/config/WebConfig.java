package com.epam.esm.config;

import com.epam.esm.pool.PersistenceConfig;
import com.epam.esm.service.ServiceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.epam.esm")
@Import({ServiceConfig.class})
public class WebConfig {
}
