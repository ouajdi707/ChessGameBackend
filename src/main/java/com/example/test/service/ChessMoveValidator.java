package com.example.test.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChessMoveValidator {

    public boolean isValidMove(String fromSquare, String toSquare, String[][] board, boolean isWhiteTurn) {
        try {
            if (fromSquare == null || toSquare == null || fromSquare.length() != 2 || toSquare.length() != 2) {
                return false;
            }

            int fromRow = 8 - Integer.parseInt(fromSquare.substring(1));
            int fromCol = fromSquare.charAt(0) - 'a';
            int toRow = 8 - Integer.parseInt(toSquare.substring(1));
            int toCol = toSquare.charAt(0) - 'a';

            if (fromRow < 0 || fromRow > 7 || fromCol < 0 || fromCol > 7 ||
                toRow < 0 || toRow > 7 || toCol < 0 || toCol > 7) {
                return false;
            }

            if (fromRow == toRow && fromCol == toCol) {
                return false; // Can't move to same square
            }

            String piece = board[fromRow][fromCol];
            if (piece == null || piece.isEmpty()) {
                return false;
            }

            // Check if piece belongs to current player
            boolean isWhitePiece = isWhitePiece(piece);
            if (isWhitePiece != isWhiteTurn) {
                return false;
            }

            // Check if destination has own piece
            String destPiece = board[toRow][toCol];
            if (destPiece != null && !destPiece.isEmpty()) {
                boolean destIsWhite = isWhitePiece(destPiece);
                if (destIsWhite == isWhiteTurn) {
                    return false; // Can't capture own piece
                }
            }

            // Validate move based on piece type
            return isValidPieceMove(piece, fromRow, fromCol, toRow, toCol, board);
        } catch (Exception e) {
            System.err.println("Error validating move: " + e.getMessage());
            return false;
        }
    }

    private boolean isWhitePiece(String piece) {
        // White pieces: ♙♖♘♗♕♔ (uppercase-like)
        // Black pieces: ♟♜♞♝♛♚ (lowercase-like)
        return piece.equals("♙") || piece.equals("♖") || piece.equals("♘") || 
               piece.equals("♗") || piece.equals("♕") || piece.equals("♔");
    }

    private boolean isValidPieceMove(String piece, int fromRow, int fromCol, int toRow, int toCol, String[][] board) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        switch (piece) {
            case "♙": // White Pawn
                return isValidWhitePawnMove(fromRow, fromCol, toRow, toCol, board);
            case "♟": // Black Pawn
                return isValidBlackPawnMove(fromRow, fromCol, toRow, toCol, board);
            case "♖": case "♜": // Rook
                return isValidRookMove(fromRow, fromCol, toRow, toCol, board);
            case "♘": case "♞": // Knight
                return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
            case "♗": case "♝": // Bishop
                return rowDiff == colDiff && isPathClear(fromRow, fromCol, toRow, toCol, board);
            case "♕": case "♛": // Queen
                return (rowDiff == colDiff || rowDiff == 0 || colDiff == 0) && 
                       isPathClear(fromRow, fromCol, toRow, toCol, board);
            case "♔": case "♚": // King
                return rowDiff <= 1 && colDiff <= 1;
            default:
                return false;
        }
    }

    private boolean isValidWhitePawnMove(int fromRow, int fromCol, int toRow, int toCol, String[][] board) {
        // Can only move forward (decreasing row)
        if (toRow >= fromRow) return false;

        int rowDiff = fromRow - toRow;
        int colDiff = Math.abs(toCol - fromCol);

        // Move forward one square
        if (colDiff == 0 && rowDiff == 1 && board[toRow][toCol] == null) {
            return true;
        }

        // Move forward two squares from starting position
        if (colDiff == 0 && rowDiff == 2 && fromRow == 6 && board[toRow][toCol] == null && 
            board[fromRow - 1][fromCol] == null) {
            return true;
        }

        // Capture diagonally
        if (colDiff == 1 && rowDiff == 1 && board[toRow][toCol] != null && 
            !isWhitePiece(board[toRow][toCol])) {
            return true;
        }

        return false;
    }

    private boolean isValidBlackPawnMove(int fromRow, int fromCol, int toRow, int toCol, String[][] board) {
        // Can only move forward (increasing row)
        if (toRow <= fromRow) return false;

        int rowDiff = toRow - fromRow;
        int colDiff = Math.abs(toCol - fromCol);

        // Move forward one square
        if (colDiff == 0 && rowDiff == 1 && board[toRow][toCol] == null) {
            return true;
        }

        // Move forward two squares from starting position
        if (colDiff == 0 && rowDiff == 2 && fromRow == 1 && board[toRow][toCol] == null && 
            board[fromRow + 1][fromCol] == null) {
            return true;
        }

        // Capture diagonally
        if (colDiff == 1 && rowDiff == 1 && board[toRow][toCol] != null && 
            isWhitePiece(board[toRow][toCol])) {
            return true;
        }

        return false;
    }

    private boolean isValidRookMove(int fromRow, int fromCol, int toRow, int toCol, String[][] board) {
        // Rook moves horizontally or vertically
        if (fromRow != toRow && fromCol != toCol) {
            return false;
        }
        return isPathClear(fromRow, fromCol, toRow, toCol, board);
    }

    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol, String[][] board) {
        int rowStep = (toRow > fromRow) ? 1 : (toRow < fromRow) ? -1 : 0;
        int colStep = (toCol > fromCol) ? 1 : (toCol < fromCol) ? -1 : 0;

        int currentRow = fromRow + rowStep;
        int currentCol = fromCol + colStep;

        while (currentRow != toRow || currentCol != toCol) {
            if (board[currentRow][currentCol] != null) {
                return false;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }

        return true;
    }

    public List<String> getValidMoves(String fromSquare, String[][] board, boolean isWhiteTurn) {
        List<String> validMoves = new ArrayList<>();
        
        for (char file = 'a'; file <= 'h'; file++) {
            for (char rank = '1'; rank <= '8'; rank++) {
                String toSquare = "" + file + rank;
                if (isValidMove(fromSquare, toSquare, board, isWhiteTurn)) {
                    validMoves.add(toSquare);
                }
            }
        }
        
        return validMoves;
    }
}

