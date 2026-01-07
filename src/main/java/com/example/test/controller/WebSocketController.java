package com.example.test.controller;

import com.example.test.dto.InvitationRequest;
import com.example.test.dto.InvitationResponse;
import com.example.test.entity.Game;
import com.example.test.service.GameService;
import com.example.test.service.OnlineUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private OnlineUserService onlineUserService;

    @Autowired
    private GameService gameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/user.connect")
    @SendTo("/topic/users.online")
    public String handleUserConnect(String username) {
        onlineUserService.addUser(username);
        return username;
    }

    @MessageMapping("/user.disconnect")
    @SendTo("/topic/users.offline")
    public String handleUserDisconnect(String username) {
        onlineUserService.removeUser(username);
        return username;
    }

    @MessageMapping("/invitation.send")
    public void sendInvitation(InvitationRequest request) {
        // Send invitation to the target user
        InvitationResponse response = new InvitationResponse(
                "INVITATION",
                request.getFromUsername(),
                request.getToUsername(),
                request.getFromUsername() + " wants to play chess with you!"
        );
        messagingTemplate.convertAndSendToUser(
                request.getToUsername(),
                "/queue/invitations",
                response
        );
    }

    @MessageMapping("/invitation.accept")
    public void acceptInvitation(InvitationRequest request) {
        // Create game
        Game game = gameService.createGame(request.getFromUsername(), request.getToUsername());

        // Notify both players
        InvitationResponse response = new InvitationResponse(
                "INVITATION_ACCEPTED",
                request.getToUsername(),
                request.getFromUsername(),
                "Game started! Game ID: " + game.getId()
        );
        response.setGameId(game.getId());

        // Send to both players
        messagingTemplate.convertAndSendToUser(
                request.getFromUsername(),
                "/queue/invitations",
                response
        );
        messagingTemplate.convertAndSendToUser(
                request.getToUsername(),
                "/queue/invitations",
                response
        );
    }

    @MessageMapping("/invitation.decline")
    public void declineInvitation(InvitationRequest request) {
        InvitationResponse response = new InvitationResponse(
                "INVITATION_DECLINED",
                request.getToUsername(),
                request.getFromUsername(),
                request.getToUsername() + " declined your invitation"
        );
        messagingTemplate.convertAndSendToUser(
                request.getFromUsername(),
                "/queue/invitations",
                response
        );
    }
}

