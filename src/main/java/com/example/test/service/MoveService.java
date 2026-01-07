package com.example.test.service;

import com.example.test.entity.Game;
import com.example.test.entity.Move;
import com.example.test.entity.User;
import com.example.test.repository.GameRepository;
import com.example.test.repository.MoveRepository;
import com.example.test.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoveService {
    @Autowired
    private MoveRepository moveRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChessMoveValidator chessMoveValidator;

    @Autowired
    private BoardService boardService;

    public Move makeMove(Long gameId, String username, String fromSquare, String toSquare) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        User player = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if it's player's turn
        if (!game.getCurrentPlayer().getUsername().equals(username)) {
            throw new RuntimeException("Not your turn");
        }

        // Get all moves to build current board state
        List<Move> moves = moveRepository.findByGameOrderByMoveNumberAsc(game);
        String[][] board = boardService.buildBoardFromMoves(moves);

        // Determine if it's white's turn (player1 is white, player2 is black)
        boolean isWhiteTurn = game.getCurrentPlayer().getId().equals(game.getPlayer1().getId());

        // Validate move using chess rules
        if (!chessMoveValidator.isValidMove(fromSquare, toSquare, board, isWhiteTurn)) {
            throw new RuntimeException("Invalid move: This piece cannot move to that square");
        }

        // Get last move to determine move number
        Move lastMove = moveRepository.findTopByGameOrderByMoveNumberDesc(game);
        int moveNumber = (lastMove == null) ? 1 : lastMove.getMoveNumber() + 1;

        // Create move
        Move move = new Move(game, player, fromSquare, toSquare, moveNumber);
        move = moveRepository.save(move);

        // Switch turn to the other player
        if (game.getCurrentPlayer().getId().equals(game.getPlayer1().getId())) {
            game.setCurrentPlayer(game.getPlayer2());
        } else {
            game.setCurrentPlayer(game.getPlayer1());
        }
        gameRepository.save(game);

        return move;
    }

    public List<Move> getGameMoves(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        return moveRepository.findByGameOrderByMoveNumberAsc(game);
    }

    public List<String> getValidMoves(Long gameId, String fromSquare, String username) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        User player = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if it's player's turn
        if (!game.getCurrentPlayer().getUsername().equals(username)) {
            throw new RuntimeException("Not your turn");
        }

        // Get all moves to build current board state
        List<Move> moves = moveRepository.findByGameOrderByMoveNumberAsc(game);
        String[][] board = boardService.buildBoardFromMoves(moves);

        // Determine if it's white's turn (player1 is white, player2 is black)
        boolean isWhiteTurn = game.getCurrentPlayer().getId().equals(game.getPlayer1().getId());

        // Get valid moves for the selected square
        return chessMoveValidator.getValidMoves(fromSquare, board, isWhiteTurn);
    }
}

