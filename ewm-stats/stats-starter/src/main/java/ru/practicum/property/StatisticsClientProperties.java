package ru.practicum.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "statistics")
public class StatisticsClientProperties {

    private String serverUrl;

    public String getUrl() {
        return serverUrl;
    }

    public void setUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
