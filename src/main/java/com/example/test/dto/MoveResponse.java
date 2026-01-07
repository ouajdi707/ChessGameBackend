package com.example.test.dto;

public class MoveResponse {
    private Long moveId;
    private Long gameId;
    private String username;
    private String fromSquare;
    private String toSquare;
    private Integer moveNumber;
    private String message;
    private boolean valid;
    private String gameStatus; // ACTIVE, FINISHED, ABANDONED
    private String winner; // Winner username if game is finished
    private boolean isCheckmate; // True if this move resulted in checkmate

    public MoveResponse() {
    }

    public MoveResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public Long getMoveId() {
        return moveId;
    }

    public void setMoveId(Long moveId) {
        this.moveId = moveId;
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

    public Integer getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(Integer moveNumber) {
        this.moveNumber = moveNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public boolean isCheckmate() {
        return isCheckmate;
    }

    public void setCheckmate(boolean isCheckmate) {
        this.isCheckmate = isCheckmate;
    }
}

