package com.example.test.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player1_id", nullable = false)
    private User player1;

    @ManyToOne
    @JoinColumn(name = "player2_id", nullable = false)
    private User player2;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status;

    @ManyToOne
    @JoinColumn(name = "current_player_id")
    private User currentPlayer; // Whose turn it is

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner; // Winner of the game (if finished)

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = GameStatus.ACTIVE;
        }
    }

    // Constructors
    public Game() {
    }

    public Game(User player1, User player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.status = GameStatus.ACTIVE;
        this.currentPlayer = player1; // Player1 starts
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(User currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public enum GameStatus {
        ACTIVE,
        FINISHED,
        ABANDONED
    }
}

