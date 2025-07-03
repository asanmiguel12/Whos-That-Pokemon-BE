package com.example.pokemon;

import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    @GetMapping("/checkRandomPokemon/{guess}")
    public Map<String, Object> checkRandomPokemon(@PathVariable Map<String, String> request) {
        String guess = request.get("guess");
        if (guess == null || guess.trim().isEmpty()) {
            return Map.of("error", "Invalid input");
        }
        try {
            String filePath = "stored_pokemon.txt";
            String storedRandomPokemon;
            try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
                long fileLength = file.length();
                if (fileLength == 0) {
                    return Map.of("error", "No Pokémon stored");
                }
                long pointer = fileLength - 1;
                file.seek(pointer);
                while (pointer > 0) {
                    pointer--;
                    file.seek(pointer);
                    if (file.readByte() == '\n') {
                        break;
                    }
                }
                storedRandomPokemon = file.readLine();
            }
            if (storedRandomPokemon == null || storedRandomPokemon.trim().isEmpty()) {
                return Map.of("error", "No Pokémon stored");
            }
            // Compare the guess with the stored Pokémon
            boolean isCorrect = storedRandomPokemon.equalsIgnoreCase(guess.trim());
            return Map.of("correct", isCorrect, "storedRandomPokemon", storedRandomPokemon);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Error reading stored Pokémon");
        }
    }

    @PostMapping("/storeRandomPokemon/{name}")
    public ResponseEntity<String> receiveRandomPokemon(@PathVariable String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid data received");
        }
        // Remove ".png" from the name if present
        name = name.replace(".png", "");
        try {
            String filePath = "stored_pokemon.txt";
            // Append only the Pokémon name to the file
            Files.write(Paths.get(filePath), (name + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return ResponseEntity.ok("Pokemon name stored successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error storing Pokemon name");
        }
    }
}
