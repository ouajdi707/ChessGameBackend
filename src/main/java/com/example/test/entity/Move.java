package com.example.test.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "moves")
public class Move {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private User player;

    @Column(nullable = false)
    private String fromSquare; // e.g., "e2"

    @Column(nullable = false)
    private String toSquare; // e.g., "e4"

    @Column(name = "move_number", nullable = false)
    private Integer moveNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public Move() {
    }

    public Move(Game game, User player, String fromSquare, String toSquare, Integer moveNumber) {
        this.game = game;
        this.player = player;
        this.fromSquare = fromSquare;
        this.toSquare = toSquare;
        this.moveNumber = moveNumber;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public String getFromSquare() {
        return fromSquare;
    }

    public void setFromSquare(String fromSquare) {
        this.fromSquare = fromSquare;
    }

    public String getToSquare() {
        return toSquare;
    }

    public void setToSquare(String toSquare) {
        this.toSquare = toSquare;
    }

    public Integer getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(Integer moveNumber) {
        this.moveNumber = moveNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

