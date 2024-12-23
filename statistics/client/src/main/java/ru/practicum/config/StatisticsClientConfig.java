package ru.practicum.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.StatisticsClient;

@Configuration
public class StatisticsClientConfig {

    @Value("${stats-server.url}")
    private String statsServerUrl;

    @Bean
    public StatisticsClient statsClient() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return new StatisticsClient(builder.build(), statsServerUrl);
    }
}
