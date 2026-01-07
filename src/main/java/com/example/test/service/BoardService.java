package com.example.test.service;

import com.example.test.entity.Move;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    public String[][] initializeBoard() {
        String[][] board = new String[8][8];
        
        // Black pieces (top)
        board[0][0] = "♜"; board[0][1] = "♞"; board[0][2] = "♝"; board[0][3] = "♛";
        board[0][4] = "♚"; board[0][5] = "♝"; board[0][6] = "♞"; board[0][7] = "♜";
        for (int i = 0; i < 8; i++) {
            board[1][i] = "♟";
        }
        
        // White pieces (bottom)
        for (int i = 0; i < 8; i++) {
            board[6][i] = "♙";
        }
        board[7][0] = "♖"; board[7][1] = "♘"; board[7][2] = "♗"; board[7][3] = "♕";
        board[7][4] = "♔"; board[7][5] = "♗"; board[7][6] = "♘"; board[7][7] = "♖";
        
        return board;
    }

    public String[][] buildBoardFromMoves(List<Move> moves) {
        String[][] board = initializeBoard();
        
        for (Move move : moves) {
            int fromRow = 8 - Integer.parseInt(move.getFromSquare().substring(1));
            int fromCol = move.getFromSquare().charAt(0) - 'a';
            int toRow = 8 - Integer.parseInt(move.getToSquare().substring(1));
            int toCol = move.getToSquare().charAt(0) - 'a';
            
            String piece = board[fromRow][fromCol];
            board[fromRow][fromCol] = null;
            board[toRow][toCol] = piece;
        }
        
        return board;
    }

    public boolean isWhiteTurn(int moveCount) {
        return moveCount % 2 == 0; // Even moves = white, odd = black
    }

    public String[][] copyBoard(String[][] board) {
        String[][] copy = new String[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        }
        return copy;
    }
}

