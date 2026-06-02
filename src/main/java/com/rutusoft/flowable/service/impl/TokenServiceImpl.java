package com.rutusoft.flowable.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rutusoft.flowable.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    @Value("${mayan.base-url}")
    private String baseUrl;

    @Value("${mayan.username}")
    private String username;

    @Value("${mayan.password}")
    private String password;

    private String cachedToken;

    private long tokenTime;

    private String AUTH_URL;

    private final WebClient webClient;

    private final ObjectMapper mapper = new ObjectMapper();

    public TokenServiceImpl(WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder.build();
    }

    @PostConstruct
    public void init() {

        AUTH_URL = baseUrl.replaceAll("/$", "") + "/auth/token/obtain/";

        log.info("AUTH_URL: {}", AUTH_URL);
    }

    @Override
    public String getToken() {

        try {

            // Reuse cached token for 50 mins
            if (cachedToken != null &&
                    (System.currentTimeMillis() - tokenTime) < (50 * 60 * 1000)) {

                log.info("Using cached Mayan token");

                return cachedToken;
            }

            Map<String, String> body = new HashMap<>();

            body.put("username", username);
            body.put("password", password);

            log.info("Calling Mayan token API");
            log.info("Request URL: {}", AUTH_URL);
            log.info("Request Body: {}", mapper.writeValueAsString(body));

            String response = webClient
                    .post()
                    .uri(AUTH_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(body)

                    .exchangeToMono(clientResponse ->

                            clientResponse.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .map(responseBody -> {

                                        HttpStatus status =
                                                (HttpStatus) clientResponse.statusCode();

                                        log.info("Mayan Status Code: {}", status.value());

                                        log.info("Mayan Response Body: {}",
                                                responseBody);

                                        if (status.isError()) {

                                            throw new RuntimeException(
                                                    "Mayan API Error | HTTP "
                                                            + status.value()
                                                            + " | Response: "
                                                            + responseBody
                                            );
                                        }

                                        return responseBody;
                                    })
                    )
                    .block();

            log.info("Raw Success Response: {}", response);

            JsonNode jsonNode = mapper.readTree(response);

            if (jsonNode.get("token") == null) {

                throw new RuntimeException(
                        "Token field missing in response: " + response
                );
            }

            cachedToken = jsonNode.get("token").asText();

            tokenTime = System.currentTimeMillis();

            log.info("Fetched Mayan token successfully");

            return cachedToken;

        } catch (Exception ex) {

            log.error("Token fetch failed", ex);

            throw new RuntimeException(
                    "Failed to fetch Mayan token",
                    ex
            );
        }
    }
}