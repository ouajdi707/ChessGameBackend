package com.example.test.service;

import com.example.test.dto.InvitationResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InvitationService {
    private final Map<String, InvitationResponse> pendingInvitations = new ConcurrentHashMap<>();

    public void addPendingInvitation(String toUsername, InvitationResponse invitation) {
        pendingInvitations.put(toUsername, invitation);
    }

    public InvitationResponse getPendingInvitation(String username) {
        return pendingInvitations.get(username);
    }

    public InvitationResponse removePendingInvitation(String username) {
        return pendingInvitations.remove(username);
    }
}

