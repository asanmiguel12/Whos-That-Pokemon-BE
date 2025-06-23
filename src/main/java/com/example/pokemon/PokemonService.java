package com.example.pokemon;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class PokemonService {
    public List<String> getAllPokemon() {
        // Placeholder data
        return Arrays.asList("Pikachu", "Bulbasaur", "Charmander", "Squirtle");
    }
}

