package com.example.test.controller;

import com.example.test.dto.MoveRequest;
import com.example.test.dto.MoveResponse;
import com.example.test.entity.Move;
import com.example.test.service.BoardService;
import com.example.test.service.ChessMoveValidator;
import com.example.test.service.MoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/moves")
@CrossOrigin(origins = "http://localhost:4200")
public class MoveController {

    @Autowired
    private MoveService moveService;

    @Autowired
    private ChessMoveValidator chessMoveValidator;

    @Autowired
    private BoardService boardService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<?> makeMove(@RequestBody MoveRequest request) {
        try {
            Move move = moveService.makeMove(
                    request.getGameId(),
                    request.getUsername(),
                    request.getFromSquare(),
                    request.getToSquare()
            );

            MoveResponse response = new MoveResponse();
            response.setMoveId(move.getId());
            response.setGameId(move.getGame().getId());
            response.setUsername(move.getPlayer().getUsername());
            response.setFromSquare(move.getFromSquare());
            response.setToSquare(move.getToSquare());
            response.setMoveNumber(move.getMoveNumber());
            response.setValid(true);
            response.setMessage("Move successful");

            // Broadcast move to all players in the game
            messagingTemplate.convertAndSend("/topic/game/" + request.getGameId(), response);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            MoveResponse errorResponse = new MoveResponse(false, e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<MoveResponse>> getGameMoves(@PathVariable Long gameId) {
        List<Move> moves = moveService.getGameMoves(gameId);
        List<MoveResponse> responses = moves.stream().map(move -> {
            MoveResponse response = new MoveResponse();
            response.setMoveId(move.getId());
            response.setGameId(move.getGame().getId());
            response.setUsername(move.getPlayer().getUsername());
            response.setFromSquare(move.getFromSquare());
            response.setToSquare(move.getToSquare());
            response.setMoveNumber(move.getMoveNumber());
            response.setValid(true);
            return response;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/valid/{gameId}")
    public ResponseEntity<?> getValidMoves(
            @PathVariable Long gameId,
            @RequestParam String fromSquare,
            @RequestParam String username) {
        try {
            List<String> validMoves = moveService.getValidMoves(gameId, fromSquare, username);
            Map<String, Object> response = new HashMap<>();
            response.put("validMoves", validMoves);
            response.put("fromSquare", fromSquare);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
