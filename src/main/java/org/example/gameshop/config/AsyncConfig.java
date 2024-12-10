package org.example.gameshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "importExecutor")
    public ThreadPoolTaskExecutor importExecutor(ImportConfigurationProperties importProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(executor.getCorePoolSize());
        executor.setMaxPoolSize(executor.getMaxPoolSize());
        executor.setQueueCapacity(importProperties.getExecutorQueueCapacity());
        executor.initialize();
        return executor;
    }
}
