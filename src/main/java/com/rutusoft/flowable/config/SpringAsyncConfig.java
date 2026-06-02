package com.rutusoft.flowable.config;

import java.util.concurrent.Executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@Slf4j
public class SpringAsyncConfig implements AsyncConfigurer {

    @Bean(name = "emailTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); //SMTP servers throttle connections
        executor.setMaxPoolSize(50); //Parallelism without spam blocking
        executor.setQueueCapacity(200); //Prevent memory OOM
        executor.setThreadNamePrefix("email-sender-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        //Backpressure when overloaded
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        executor.initialize();
        return executor;
    }

    @Bean(name = "applicationTaskExecutor")
    public AsyncListenableTaskExecutor applicationTaskExecutor() {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(10);

        executor.setMaxPoolSize(50);

        executor.setQueueCapacity(500);

        executor.setThreadNamePrefix(
                "flowable-async-"
        );

        executor.setWaitForTasksToCompleteOnShutdown(true);

        executor.setAwaitTerminationSeconds(60);

        executor.initialize();

        log.info(
                "Initialized applicationTaskExecutor"
        );

        return executor;
    }

    @Bean(name = "emailReaderExecutor")
    public ThreadPoolTaskExecutor emailReaderExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); //SMTP servers throttle connections
        executor.setMaxPoolSize(50); //Parallelism without spam blocking
        executor.setQueueCapacity(200); //Prevent memory OOM
        executor.setThreadNamePrefix("email-reader-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        //Backpressure when overloaded
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        executor.initialize();
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) ->
               log.error("Async error in method: {}", method.getName(), ex);
    }

}
