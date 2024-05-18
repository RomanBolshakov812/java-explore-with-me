package ru.practicum.client;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;

@Component
@RequiredArgsConstructor
public class StatsClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String serverUrl = "http://stats-server:9090";

    public void addHit(EndpointHit endpointHit) {
        this.restTemplate.postForEntity(serverUrl + "/hit", endpointHit, EndpointHit.class);
    }

    public ArrayList<ViewStats> getStats(String start, String end,
                                         List<String> uris, Boolean unique) {
        String requestUrl = serverUrl + "/stats?start=" + start + "&end=" + end;
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
