package dev.interview.server.ai.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        int maxConnections = 200; // 동시 접속자 증가에 대비 (200명까지)
        int maxIdleTime = 10;  // 유휴 커넥션 시간

        ConnectionProvider provider = ConnectionProvider.builder("custom")
                .maxConnections(maxConnections)
                .maxIdleTime(Duration.ofSeconds(maxIdleTime))
                .pendingAcquireTimeout(Duration.ofSeconds(5))  // 커넥션 대기 시간
                .maxLifeTime(Duration.ofMinutes(2))            // 커넥션 최대 생명 주기
                .evictInBackground(Duration.ofSeconds(60))     // 유휴 커넥션 정리 주기
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .responseTimeout(Duration.ofSeconds(5))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5))
                                .addHandlerLast(new WriteTimeoutHandler(5))
                );

        return builder
                .baseUrl("http://localhost:6333")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)  // 수정된 부분
                .build();
    }
}
