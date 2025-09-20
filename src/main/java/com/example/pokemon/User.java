package com.example.pokemon;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private Integer highestStreak = 0;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getHighestStreak() { return highestStreak; }
    public void setHighestStreak(Integer highestStreak) { this.highestStreak = highestStreak; }
}
