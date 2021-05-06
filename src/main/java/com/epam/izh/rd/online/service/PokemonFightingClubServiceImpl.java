package com.epam.izh.rd.online.service;

import com.epam.izh.rd.online.entity.Pokemon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PokemonFightingClubServiceImpl implements PokemonFightingClubService {

    private static PokemonFightingClubServiceImpl INSTANCE;
    private final PokemonFetchingServiceImpl pokemonFetchingService;

    private PokemonFightingClubServiceImpl(PokemonFetchingServiceImpl pokemonFetchingService) {
        this.pokemonFetchingService = pokemonFetchingService;
    }

    public static PokemonFightingClubServiceImpl getInstance(PokemonFetchingServiceImpl service) {
        return INSTANCE = new PokemonFightingClubServiceImpl(service);
    }

    @Override
    public Pokemon doBattle(Pokemon p1, Pokemon p2) {

        short p1Hp = p1.getHp();
        short p2Hp = p2.getHp();

        for (int i = 0; p1Hp > 0 && p2Hp > 0; i++) {
            System.out.println("HP " + p1.getPokemonName() + " = " + p1Hp);
            System.out.println("HP " + p2.getPokemonName() + " = " + p2Hp);

            if ((i & 1) > 0) {
                doDamage(p1, p2);
            } else {
                doDamage(p2, p1);
            }

            p1Hp = p1.getHp();
            p2Hp = p2.getHp();
        }

        return p1Hp > 0 ? p1 : p2;
    }

    @Override
    public void showWinner(Pokemon winner) {
        String name = winner.getPokemonName();
        byte[] pokemonImage = pokemonFetchingService.getPokemonImage(name);
        try {
            Files.write(Paths.get(name + ".png"), pokemonImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doDamage(Pokemon from, Pokemon to) {
        to.takeDamage(from.getAttack());
    }
}
