package com.example.pokemon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@CrossOrigin("*")
@RequestMapping("api/pokemon")
public class PokemonController {

    @Autowired
    private PokemonRepository pokemonRepository;

    @GetMapping("/checkRandomPokemon/{guess}")
    public Map<String, Object> checkRandomPokemon(@PathVariable String guess) {
        if (guess == null || guess.trim().isEmpty()) {
            return Map.of("error", "Invalid input");
        }
    
        try {
            Pokemon latest = pokemonRepository.findTopByOrderByIdDesc();
            if (latest == null || latest.getName() == null || latest.getName().trim().isEmpty()) {
                return Map.of("error", "No Pokémon stored");
            }
    
            boolean isCorrect = latest.getName().equalsIgnoreCase(guess.trim());
            return Map.of("correct", isCorrect, "storedRandomPokemon", latest.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Error reading stored Pokémon");
        }
    }
    
    @PostMapping("/storeRandomPokemon/{name}")
    public ResponseEntity<String> storeRandomPokemon(@PathVariable String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid data received");
        }
    
        name = name.replace(".png", "");
    
        try {
            Pokemon pokemon = new Pokemon();
            pokemon.setName(name);
            pokemonRepository.save(pokemon);
            return ResponseEntity.ok("Pokemon name stored successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error storing Pokemon name");
        }
    }

    @GetMapping("/latestPokemon")
    public ResponseEntity<Map<String, String>> getLatestPokemon() {
        try {
            Pokemon latest = pokemonRepository.findTopByOrderByIdDesc();
            if (latest == null) {
                return ResponseEntity.ok(Map.of("message", "No Pokémon found"));
            }
            return ResponseEntity.ok(Map.of("latestPokemon", latest.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error retrieving Pokémon"));
        }
    }
    
    @PostMapping("/logStreak/{username}/{streakCount}")
    public ResponseEntity<String> logStreak(@RequestParam String username, @RequestParam int streak) {
        if (username == null || username.trim().isEmpty() || streak < 0) {
            return ResponseEntity.badRequest().body("Invalid username or streak");
        }
        String filePath = "streaks.txt";
        try {
            java.util.Map<String, Integer> streaks = new java.util.HashMap<>();
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            if (java.nio.file.Files.exists(path)) {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(path);
                for (String line : lines) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        streaks.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            }
            // Update if new streak is higher
            streaks.put(username, Math.max(streaks.getOrDefault(username, 0), streak));
            // Write all streaks back
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Integer> entry : streaks.entrySet()) {
                sb.append(entry.getKey()).append(":").append(entry.getValue()).append(System.lineSeparator());
            }
            java.nio.file.Files.write(path, sb.toString().getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
            return ResponseEntity.ok("Streak logged successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error logging streak");
        }
    }

    // Optional: Endpoint to get all highest streaks
    @GetMapping("/highestStreaks")
    public ResponseEntity<Map<String, Integer>> getHighestStreaks() {
        String filePath = "streaks.txt";
        java.util.Map<String, Integer> streaks = new java.util.HashMap<>();
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            if (java.nio.file.Files.exists(path)) {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(path);
                for (String line : lines) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        streaks.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            }
            return ResponseEntity.ok(streaks);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
