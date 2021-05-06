package com.epam.izh.rd.online.service;
import java.io.IOException;
import com.epam.izh.rd.online.entity.Pokemon;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestListener;
import com.github.tomakehurst.wiremock.http.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

class PokemonFetchingServiceImplTest {

    private static WireMockServer wireMockServer;
    private final String URL = "http://localhost:8080/";
    private PokemonFetchingServiceImpl pokemonFetchingService = PokemonFetchingServiceImpl.getInstance(URL);
    private PokemonFightingClubServiceImpl clubService = PokemonFightingClubServiceImpl.getInstance(pokemonFetchingService);

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer();
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
    }

    @Test
    void fetchByName() throws IOException {
        Pokemon p = new Pokemon(25, "pikachu", (short) 35, (short) 55,(short) 40);
        Pokemon pNew = pokemonFetchingService.fetchByName("pikachu");
        assertEquals(p, pNew);
        Pokemon pokemonNull = pokemonFetchingService.fetchByName("");
        assertNull(pokemonNull);
    }

    @Test
    void getPokemonImage() {
        byte[] bytes = pokemonFetchingService.getPokemonImage("slowpoke");
        assertTrue(bytes.length != 0);

        byte[] bytesNull = pokemonFetchingService.getPokemonImage("");
        assertEquals(0, bytesNull.length);
    }

    @AfterAll
    static void after() {
        wireMockServer.stop();
    }
}