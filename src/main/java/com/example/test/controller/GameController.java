package com.example.test.controller;

import com.example.test.entity.Game;
import com.example.test.repository.GameRepository;
import com.example.test.service.ActiveGameService;
import com.example.test.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "http://localhost:4200")
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ActiveGameService activeGameService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/{gameId}")
    public ResponseEntity<?> getGame(@PathVariable Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElse(null);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", game.getId());
        response.put("player1", game.getPlayer1().getUsername());
        response.put("player2", game.getPlayer2().getUsername());
        response.put("status", game.getStatus().toString());
        response.put("currentPlayer", game.getCurrentPlayer() != null ? game.getCurrentPlayer().getUsername() : game.getPlayer1().getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active/{username}")
    public ResponseEntity<?> getActiveGame(@PathVariable String username) {
        Long gameId = activeGameService.getActiveGame(username);
        if (gameId != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("gameId", gameId);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{gameId}/quit")
    public ResponseEntity<?> quitGame(@PathVariable Long gameId, @RequestParam String username) {
        Game game = gameRepository.findById(gameId)
                .orElse(null);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        // Set game status to abandoned
        game.setStatus(Game.GameStatus.ABANDONED);
        gameRepository.save(game);

        // Remove from active games
        activeGameService.removeActiveGame(game.getPlayer1().getUsername());
        activeGameService.removeActiveGame(game.getPlayer2().getUsername());

        // Clear any pending invitations for both players
        invitationService.removePendingInvitation(game.getPlayer1().getUsername());
        invitationService.removePendingInvitation(game.getPlayer2().getUsername());

        // Notify the other player
        String otherPlayer = game.getPlayer1().getUsername().equals(username) 
            ? game.getPlayer2().getUsername() 
            : game.getPlayer1().getUsername();

        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "PLAYER_QUIT");
        notification.put("message", username + " has left the game");
        notification.put("gameId", gameId);

        messagingTemplate.convertAndSendToUser(
            otherPlayer,
            "/queue/game-events",
            notification
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Game quit successfully");
        return ResponseEntity.ok(response);
    }
}
