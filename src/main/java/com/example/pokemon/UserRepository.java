package com.example.pokemon;

import com.example.pokemon.models.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    void save(User user);
}

