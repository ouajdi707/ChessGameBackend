package com.example.test.controller;

import com.example.test.dto.MoveRequest;
import com.example.test.dto.MoveResponse;
import com.example.test.entity.Game;
import com.example.test.entity.Move;
import com.example.test.repository.GameRepository;
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

    @Autowired
    private GameRepository gameRepository;

    @PostMapping
    public ResponseEntity<MoveResponse> makeMove(@RequestBody MoveRequest request) {
        try {
            Move move = moveService.makeMove(
                    request.getGameId(),
                    request.getUsername(),
                    request.getFromSquare(),
                    request.getToSquare()
            );

            // Reload game to get updated status
            Game game = gameRepository.findById(request.getGameId())
                    .orElseThrow(() -> new RuntimeException("Game not found"));

            MoveResponse response = new MoveResponse();
            response.setMoveId(move.getId());
            response.setGameId(move.getGame().getId());
            response.setUsername(move.getPlayer().getUsername());
            response.setFromSquare(move.getFromSquare());
            response.setToSquare(move.getToSquare());
            response.setMoveNumber(move.getMoveNumber());
            response.setValid(true);
            response.setGameStatus(game.getStatus().toString());
            
            // Check if game is finished (checkmate)
            if (game.getStatus() == Game.GameStatus.FINISHED && game.getWinner() != null) {
                response.setCheckmate(true);
                response.setWinner(game.getWinner().getUsername());
                response.setMessage("Checkmate! " + game.getWinner().getUsername() + " wins!");
            } else {
                response.setMessage("Move successful");
            }

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
    public ResponseEntity<Map<String, Object>> getValidMoves(
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
