package com.fpt.gta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ForkJoinPool;
@EnableAsync
@Configuration
public class ExecutorConfig implements AsyncConfigurer {
    @Bean
    public ForkJoinPool forkAndJoinExecutor() {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        return pool;
    }

}
