package ru.practicum;

import org.springframework.boot.web.client.RestTemplateBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        StatisticsClient statisticsClient  = new StatisticsClient(builder.build(), "http://localhost:9090");
        List<String> events = new ArrayList<>();
        events.add("/events/1");
        events.add("/events/2");
        statisticsClient.getStats("2023-05-05 00:00:00","2035-05-05 00:00:00",
                events, false);
    }
}
