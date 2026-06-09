package com.saas.luminex.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // Uses Spring's default SimpleAsyncTaskExecutor.
    // For production with high concurrency, define a custom ThreadPoolTaskExecutor bean here.
}
