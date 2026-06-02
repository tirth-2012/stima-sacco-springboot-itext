package com.rutusoft.flowable.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() throws Exception {

        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        ConnectionProvider provider =
                ConnectionProvider.builder("mayan-pool")
                        .maxConnections(200)
                        .pendingAcquireMaxCount(1000)
                        .pendingAcquireTimeout(Duration.ofSeconds(60))
                        .maxIdleTime(Duration.ofSeconds(30))
                        .maxLifeTime(Duration.ofMinutes(10))
                        .build();

        HttpClient httpClient = HttpClient.create(provider)
                .secure(t -> t.sslContext(sslContext))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                .responseTimeout(Duration.ofMinutes(5))
                .doOnConnected(conn ->
                        conn.addHandlerLast(
                                        new ReadTimeoutHandler(300, TimeUnit.SECONDS))
                                .addHandlerLast(
                                        new WriteTimeoutHandler(300, TimeUnit.SECONDS)
                                )
                );

        return WebClient.builder()
                .clientConnector(
                        new ReactorClientHttpConnector(httpClient)
                );
    }
}