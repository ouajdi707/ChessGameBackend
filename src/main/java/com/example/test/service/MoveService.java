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

        // Make a test move to check if it leaves the king in check
        String[][] testBoard = boardService.copyBoard(board);
        int fromRow = 8 - Integer.parseInt(fromSquare.substring(1));
        int fromCol = fromSquare.charAt(0) - 'a';
        int toRow = 8 - Integer.parseInt(toSquare.substring(1));
        int toCol = toSquare.charAt(0) - 'a';
        
        String movingPiece = testBoard[fromRow][fromCol];
        testBoard[fromRow][fromCol] = null;
        testBoard[toRow][toCol] = movingPiece;
        
        // Check if this move leaves the player's own king in check
        if (chessMoveValidator.isKingInCheck(testBoard, isWhiteTurn)) {
            throw new RuntimeException("Invalid move: This move would leave your king in check");
        }

        // Get last move to determine move number
        Move lastMove = moveRepository.findTopByGameOrderByMoveNumberDesc(game);
        int moveNumber = (lastMove == null) ? 1 : lastMove.getMoveNumber() + 1;

        // Create move
        Move move = new Move(game, player, fromSquare, toSquare, moveNumber);
        move = moveRepository.save(move);

        // Update board with the new move to check for checkmate
        List<Move> updatedMoves = moveRepository.findByGameOrderByMoveNumberAsc(game);
        String[][] updatedBoard = boardService.buildBoardFromMoves(updatedMoves);

        // Determine whose turn it is now (the opponent)
        boolean opponentIsWhite = !isWhiteTurn;

        // Check for checkmate on the opponent
        if (chessMoveValidator.isCheckmate(updatedBoard, opponentIsWhite)) {
            // Game is over - current player (who just moved) wins
            game.setStatus(Game.GameStatus.FINISHED);
            game.setWinner(player);
            game.setCurrentPlayer(null); // No more turns
        } else {
            // Switch turn to the other player
            if (game.getCurrentPlayer().getId().equals(game.getPlayer1().getId())) {
                game.setCurrentPlayer(game.getPlayer2());
            } else {
                game.setCurrentPlayer(game.getPlayer1());
            }
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

