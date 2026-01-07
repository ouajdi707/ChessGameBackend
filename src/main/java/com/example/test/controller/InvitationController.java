package com.example.test.controller;

import com.example.test.dto.InvitationRequest;
import com.example.test.dto.InvitationResponse;
import com.example.test.entity.Game;
import com.example.test.service.GameService;
import com.example.test.service.InvitationService;
import com.example.test.service.OnlineUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
@CrossOrigin(origins = "http://localhost:4200")
public class InvitationController {

    @Autowired
    private OnlineUserService onlineUserService;

    @Autowired
    private GameService gameService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private com.example.test.service.ActiveGameService activeGameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public ResponseEntity<InvitationResponse> sendInvitation(@RequestBody InvitationRequest request) {
        if (!onlineUserService.isUserOnline(request.getToUsername())) {
            return ResponseEntity.badRequest()
                    .body(new InvitationResponse("ERROR", request.getFromUsername(), 
                            request.getToUsername(), "User is not online"));
        }

        InvitationResponse response = new InvitationResponse(
                "INVITATION",
                request.getFromUsername(),
                request.getToUsername(),
                request.getFromUsername() + " wants to play chess with you!"
        );
        
        // Store invitation for polling
        invitationService.addPendingInvitation(request.getToUsername(), response);
        
        // Also try WebSocket (if connected)
        messagingTemplate.convertAndSendToUser(
                request.getToUsername(),
                "/queue/invitations",
                response
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accept")
    public ResponseEntity<InvitationResponse> acceptInvitation(@RequestBody InvitationRequest request) {
        // Remove pending invitation for both players
        invitationService.removePendingInvitation(request.getToUsername());
        invitationService.removePendingInvitation(request.getFromUsername());
        
        Game game = gameService.createGame(request.getFromUsername(), request.getToUsername());

        // Store active game for both players
        activeGameService.setActiveGame(request.getFromUsername(), game.getId());
        activeGameService.setActiveGame(request.getToUsername(), game.getId());

        InvitationResponse response = new InvitationResponse(
                "INVITATION_ACCEPTED",
                request.getToUsername(),
                request.getFromUsername(),
                "Game started!"
        );
        response.setGameId(game.getId());

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

        return ResponseEntity.ok(response);
    }

    @PostMapping("/decline")
    public ResponseEntity<InvitationResponse> declineInvitation(@RequestBody InvitationRequest request) {
        // Remove pending invitation
        invitationService.removePendingInvitation(request.getToUsername());
        
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
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending/{username}")
    public ResponseEntity<InvitationResponse> getPendingInvitation(@PathVariable String username) {
        InvitationResponse invitation = invitationService.getPendingInvitation(username);
        if (invitation != null) {
            return ResponseEntity.ok(invitation);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/pending/{username}")
    public ResponseEntity<Void> clearPendingInvitation(@PathVariable String username) {
        invitationService.removePendingInvitation(username);
        return ResponseEntity.ok().build();
    }
}

