package ru.practicum.client;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.EndpointHit;

@Service
public class StatsClient {

    private final RestTemplate restTemplate;

    public StatsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void addHit(EndpointHit endpointHit) {
        this.restTemplate.postForEntity("/hit", endpointHit, EndpointHit.class);
    }

    public ResponseEntity<Object> getStats(String start, String end,
                                           List<String> uris, Boolean unique) {
        String urisAsParam = "";
        String uniqueAsParam = "";
        if (uris != null) {
            urisAsParam = "&uris=" + uris.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
        }
        if (unique) {
            uniqueAsParam = "&unique=true";
        }
        return restTemplate.getForEntity("/stats?start={start}&end={end}"
                + urisAsParam + uniqueAsParam, Object.class);
    }
}
