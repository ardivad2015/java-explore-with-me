package ru.practicum.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.StatisticsClient;
import ru.practicum.property.StatisticsClientProperties;

@Configuration
@EnableConfigurationProperties(StatisticsClientProperties.class)
public class StatisticsClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(StatisticsClient.class)
    public StatisticsClient statisticsClient(StatisticsClientProperties statisticsClientProperties) {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return new StatisticsClient(builder.build(), statisticsClientProperties.getUrl());
    }
}
