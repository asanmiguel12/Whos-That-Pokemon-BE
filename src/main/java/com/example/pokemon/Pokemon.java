package com.example.pokemon;

import jakarta.persistence.*;

@Entity
@Table(name = "pokemon_names")
public class Pokemon {
    // Test comment for Git auto-staging
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

}
