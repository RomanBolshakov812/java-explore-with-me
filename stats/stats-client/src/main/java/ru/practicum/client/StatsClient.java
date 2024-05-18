package ru.practicum.client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;

import javax.websocket.ClientEndpoint;

@Component
@RequiredArgsConstructor
public class StatsClient {

    private final RestTemplate restTemplate = new RestTemplate();
    //@Value("${stats-server.url}") String serverUrl;
    //String serverUrl = "http://localhost:9090";
    String serverUrl = "http://stats-server:9090";
    private static  final DateTimeFormatter DATE_TIME_FORMATTER///////////////////////////////
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");/////////////////////////////

    public void addHit(EndpointHit endpointHit) {
        this.restTemplate.postForEntity(serverUrl + "/hit", endpointHit, EndpointHit.class);
    }

    public ArrayList<ViewStats> getStats(String start, String end,
                                         List<String> uris, Boolean unique) {
//        LocalDateTime startS = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
//        LocalDateTime endE = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
//        start = startS.format(DATE_TIME_FORMATTER);
//        end = endE.format(DATE_TIME_FORMATTER);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        String startDate = java.net.URLEncoder.encode(start, StandardCharsets.UTF_8);
        String requestUrl = serverUrl + "/stats?start=" + start + "&end=" + end;
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //String requestUrl = serverUrl + "/stats?start={" + start + "}&end={" + end + "}";
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



//@Component
//@RequiredArgsConstructor
//public class StatsClient {
//
//    private final RestTemplate restTemplate = new RestTemplate();
//    String serverUrl = "http://localhost:9090";
//    private static  final DateTimeFormatter DATE_TIME_FORMATTER///////////////////////////////
//            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");/////////////////////////////
//
//    public void addHit(EndpointHit endpointHit) {
//        this.restTemplate.postForEntity(serverUrl + "/hit", endpointHit, EndpointHit.class);
//    }
//
//    public ArrayList<ViewStats> getStats(String start, String end,
//                                              List<String> uris, Boolean unique) {
////        LocalDateTime startS = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
////        LocalDateTime endE = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
////        start = startS.format(DATE_TIME_FORMATTER);
////        end = endE.format(DATE_TIME_FORMATTER);
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////        String startDate = java.net.URLEncoder.encode(start, StandardCharsets.UTF_8);
//        String requestUrl = serverUrl + "/stats?start=" + start + "&end=" + end;
//        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        //String requestUrl = serverUrl + "/stats?start={" + start + "}&end={" + end + "}";
//        if (uris != null && !uris.isEmpty()) {
//            String urisAsParam = "&uris=" + uris.stream()
//                    .map(Object::toString)
//                    .collect(Collectors.joining(","));
//            requestUrl = requestUrl + urisAsParam;
//        }
//        if (unique == null) {
//            unique = false;
//        }
//        if (unique) {
//            requestUrl = requestUrl + "&unique=true";
//        }
//
//        ResponseEntity<ArrayList<ViewStats>> response = restTemplate.exchange(
//                requestUrl,
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<>() {
//        });
//        return response.getBody();
//    }
//}
