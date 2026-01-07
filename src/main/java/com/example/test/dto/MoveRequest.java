package com.example.test.dto;

public class MoveRequest {
    private Long gameId;
    private String username;
    private String fromSquare;
    private String toSquare;

    public MoveRequest() {
    }

    public MoveRequest(Long gameId, String username, String fromSquare, String toSquare) {
        this.gameId = gameId;
        this.username = username;
        this.fromSquare = fromSquare;
        this.toSquare = toSquare;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}

