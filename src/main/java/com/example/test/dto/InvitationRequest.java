package com.example.test.dto;

public class InvitationRequest {
    private String fromUsername;
    private String toUsername;

    public InvitationRequest() {
    }

    public InvitationRequest(String fromUsername, String toUsername) {
        this.fromUsername = fromUsername;
        this.toUsername = toUsername;
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
}

