package com.example.test.dto;

public class InvitationResponse {
    private String type; // "INVITATION", "INVITATION_ACCEPTED", "INVITATION_DECLINED"
    private String fromUsername;
    private String toUsername;
    private Long gameId;
    private String message;

    public InvitationResponse() {
    }

    public InvitationResponse(String type, String fromUsername, String toUsername, String message) {
        this.type = type;
        this.fromUsername = fromUsername;
        this.toUsername = toUsername;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

