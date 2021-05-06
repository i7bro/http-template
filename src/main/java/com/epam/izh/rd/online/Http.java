package com.epam.izh.rd.online;

import com.epam.izh.rd.online.entity.Pokemon;
import com.epam.izh.rd.online.service.PokemonFetchingServiceImpl;
import com.epam.izh.rd.online.service.PokemonFightingClubServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class Http {
    public static void main(String[] args) throws IOException {
//        https://pokeapi.co/api/v2/
//        http://localhost:8080/
        PokemonFetchingServiceImpl service = PokemonFetchingServiceImpl.getInstance("http://localhost:8080/");
        PokemonFightingClubServiceImpl clubService = PokemonFightingClubServiceImpl.getInstance(service);

        WireMockServer wireMockServer = new WireMockServer();

        wireMockServer.start();
        stubFor(get(urlEqualTo("/pikachu"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"name\":\"pikachu\", \"id\":25, \"stats\":[{\"base_stat\":35}, {\"base_stat\":55}, {\"base_stat\":40}], \"sprites\":{\"front_default\":\"http://localhost:8080/pikachu_file\"}}")));
        stubFor(get(urlEqualTo("/slowpoke"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withBody("{\"name\":\"slowpoke\", \"id\":79, \"stats\":[{\"base_stat\":90}, {\"base_stat\":65}, {\"base_stat\":65}], \"sprites\":{\"front_default\":\"http://localhost:8080/slowpoke_file\"}}")));
        stubFor(get(urlEqualTo("/slowpoke_file"))
                .willReturn(aResponse()
                        .withBodyFile("slowpoke.png")));
        stubFor(get(urlEqualTo("/pikachu_file"))
                .willReturn(aResponse()
                        .withBodyFile("pikachu.png")));


        Pokemon pikachu = service.fetchByName("pikachu");
        Pokemon slowpoke = service.fetchByName("slowpoke");

        Pokemon winner = pikachu.getPokemonId() > slowpoke.getPokemonId() ?
                clubService.doBattle(pikachu, slowpoke) :
                clubService.doBattle(slowpoke, pikachu);

        byte[] pikachus = service.getPokemonImage("pikachu");
        System.out.println(pikachus.length);
//        clubService.showWinner(winner);
//
        wireMockServer.stop();
    }
}
