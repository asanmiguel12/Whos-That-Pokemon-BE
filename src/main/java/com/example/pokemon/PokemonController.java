package com.example.pokemon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@CrossOrigin("*")
@RequestMapping("api/pokemon")
public class PokemonController {

    @Autowired
    private PokemonRepository pokemonRepository;
    
    @Autowired
    private UserRepository userRepository;

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
    
    @PostMapping("/logStreak")
    public ResponseEntity<String> logStreak(@RequestParam String username, @RequestParam int streak) {
        if (username == null || username.trim().isEmpty() || streak < 0) {
            return ResponseEntity.badRequest().body("Invalid username or streak");
        }
        
        try {
            // Find existing user or create new one
            User user = userRepository.findByUsername(username).orElse(new User());
            user.setUsername(username);
            
            // Update streak only if new streak is higher
            if (streak > user.getHighestStreak()) {
                user.setHighestStreak(streak);
            }
            
            userRepository.save(user);
            return ResponseEntity.ok("Streak logged successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error logging streak");
        }
    }

    // Get all highest streaks from database
    @GetMapping("/highestStreaks")
    public ResponseEntity<Map<String, Integer>> getHighestStreaks() {
        try {
            java.util.Map<String, Integer> streaks = new java.util.HashMap<>();
            userRepository.findAll().forEach(user -> 
                streaks.put(user.getUsername(), user.getHighestStreak())
            );
            return ResponseEntity.ok(streaks);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
    
    // Get specific user's streak
    @GetMapping("/user/{username}/streak")
    public ResponseEntity<Map<String, Object>> getUserStreak(@PathVariable String username) {
        try {
            return userRepository.findByUsername(username)
                .map(user -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("username", user.getUsername());
                    response.put("highestStreak", user.getHighestStreak());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error retrieving user streak"));
        }
    }
}
