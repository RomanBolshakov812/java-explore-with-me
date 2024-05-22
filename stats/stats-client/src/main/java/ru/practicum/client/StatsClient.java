package ru.practicum.client;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;

@Component
public class StatsClient {

    private final RestTemplate restTemplate;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String userServiceUrl,
                       RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(userServiceUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void addHit(EndpointHit endpointHit) {
        restTemplate.postForEntity("/hit", endpointHit, EndpointHit.class);
    }

    public ArrayList<ViewStats> getStats(String start, String end,
                                         List<String> uris, Boolean unique) {
        String requestUrl = "/stats?start=" + start + "&end=" + end;
        if (uris != null && !uris.isEmpty()) {
            String urisAsParam = "&uris=" + uris.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
            requestUrl = requestUrl + urisAsParam;
        }
        if (unique == null) {
            unique = false;
        }
        if (unique) {
            requestUrl = requestUrl + "&unique=true";
        }

        ResponseEntity<ArrayList<ViewStats>> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
