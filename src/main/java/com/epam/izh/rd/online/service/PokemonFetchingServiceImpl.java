package com.epam.izh.rd.online.service;

import com.epam.izh.rd.online.entity.Pokemon;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class PokemonFetchingServiceImpl implements PokemonFetchingService {

    private static PokemonFetchingServiceImpl INSTANCE;
    private final RestTemplate REST_TEMPLATE;
    private final String URL;

    private PokemonFetchingServiceImpl(String URL) {
        this.URL = URL;
        this.REST_TEMPLATE = new RestTemplate();
    }

    public static PokemonFetchingServiceImpl getInstance(String URL) {
        return INSTANCE = new PokemonFetchingServiceImpl(URL);
    }

    private JsonNode getJsonNode(String name) {
        String url = URL + name;
        try {
            ResponseEntity<String> forEntity = REST_TEMPLATE.getForEntity(url, String.class);
            if (forEntity.getStatusCode().equals(HttpStatus.OK)) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    return objectMapper.readTree(forEntity.getBody());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Pokemon fetchByName(String name) throws IllegalArgumentException {
        JsonNode jsonNode = getJsonNode(name);
        System.out.println(jsonNode);

        if (jsonNode == null) return null;
        return new Pokemon(
                jsonNode.path("id").asLong(),
                jsonNode.path("name").asText(),
                (short) jsonNode.path("stats").path(0).path("base_stat").asInt(),
                (short) jsonNode.path("stats").path(1).path("base_stat").asInt(),
                (short) jsonNode.path("stats").path(2).path("base_stat").asInt()
        );
    }

    @Override
    public byte[] getPokemonImage(String name) throws IllegalArgumentException {
        JsonNode root = getJsonNode(name);
        if (root == null) return new byte[0];
        String urlImg = root.path("sprites").path("front_default").asText();
        ResponseEntity<byte[]> forEntity = REST_TEMPLATE.getForEntity(urlImg, byte[].class);

        return forEntity.getBody();
    }
}
