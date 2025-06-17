package Engine;

import Model.ChessBoard;
import Model.ChessMove;
import Model.ChessPiece;
import Records.Position;

public class SpecialMoveHandler {
    private ChessBoard chessBoard;
    private MoveValidator moveValidator;
    private SafetyChecker safetyChecker;
    private BoardAnalyzer boardAnalyzer;

    public SpecialMoveHandler(ChessBoard chessBoard, MoveValidator moveValidator, SafetyChecker safetyChecker, BoardAnalyzer boardAnalyzer) {
        this.chessBoard = chessBoard;
        this.moveValidator = moveValidator;
        this.safetyChecker = safetyChecker;
        this.boardAnalyzer = boardAnalyzer;
    }

    public boolean CanCastle(ChessMove move) {
        // Extract the key information
        int kingRow = move.fromRow;
        int kingCol = move.fromCol;
        boolean isKingSideCastling = move.toCol == 6;

        // 1. Verify the king hasn't moved (using the isMoved flag)
        ChessPiece king = chessBoard.board[kingRow][kingCol];
        if (king == null || !king.getType().equals("King") || king.IsMoved()) {
            return false;  // King is missing or has moved
        }

        // 2. Identify and check the appropriate rook
        int rookCol = isKingSideCastling ? 7 : 0;  // H-file or A-file
        ChessPiece rook = chessBoard.board[kingRow][rookCol];
        if (rook == null || !rook.getType().equals("Rook") || rook.IsMoved()) {
            return false;  // Rook is missing or has moved
        }

        // 3. Check if the path between king and rook is clear
        int startCol = Math.min(kingCol, rookCol) + 1;
        int endCol = Math.max(kingCol, rookCol);
        for (int col = startCol; col < endCol; col++) {
            if (chessBoard.board[kingRow][col] != null) {
                return false;  // Path is blocked
            }
        }

        // 4. Check if the king's path (including destination) is safe
        int step = isKingSideCastling ? 1 : -1;
        for (int col = kingCol; col != move.toCol + step; col += step) {
            if (!safetyChecker.IsSquareSafeForKing(move.color, kingRow, col)) {
                return false;  // King would move through or into check
            }
        }

        return true;  // All castling conditions are met
    }

    public boolean CanCheck(String color, String type, int fromRow, int fromCol, int toRow, int toCol, boolean isPromotion) {
        // En-passant is not yet implemented

        if (!isPromotion && !(moveValidator.CanMove(color, type, fromRow, fromCol, toRow, toCol)) || moveValidator.CanCapture(color, type, fromRow, fromCol, toRow, toCol, false)) {
            return false;
        }

        // 3. Find the opponent's king
        String opponentColor = color.equals("white") ? "black" : "white";
        Position kingsPosition = boardAnalyzer.GetCandidatePositions(opponentColor, "King").size() == 1 ? boardAnalyzer.GetCandidatePositions(opponentColor, "King").get(0) : null;
        if (kingsPosition == null) {
            // opponents king is not found or more than 2 enemy Kings are present at the board
            return false;
        }
        int opponentKingRow = kingsPosition.row();
        int opponentKingCol = kingsPosition.col();

        if (isPromotion) {
            return !safetyChecker.IsSquareSafeForKing(opponentColor, opponentKingRow, opponentKingCol);
        }


        // temporary update squares
        ChessPiece movingPiece = chessBoard.board[fromRow][fromCol];
        ChessPiece capturedPiece = chessBoard.board[toRow][toCol];

        // 2. Temporarily make the move
        chessBoard.board[toRow][toCol] = movingPiece;
        chessBoard.board[fromRow][fromCol] = null;


        // return Pieces  to their  locations

        if (safetyChecker.IsSquareSafeForKing(opponentColor, opponentKingRow, opponentKingCol)) {
            chessBoard.board[fromRow][fromCol] = movingPiece;
            chessBoard.board[toRow][toCol] = capturedPiece;
            return false;
        }
        chessBoard.board[fromRow][fromCol] = movingPiece;
        chessBoard.board[toRow][toCol] = capturedPiece;
        return true;
    }

    public boolean CanPromote(String color, String type, int fromRow, int fromCol, int toRow, int toCol) {
        // SECURITY CHECK 1: Only pawns can be promoted
        if (!type.equals("Pawn")) {
            return false;
        }

        // SECURITY CHECK 2: Promotion must occur on the correct rank
        int promotionRank = color.equals("white") ? 0 : 7; // Rank 8 for white, rank 1 for black
        if (toRow != promotionRank) {
            return false;
        }


        // just promotion
        return moveValidator.canPawnMove(color, fromRow, fromCol, toRow, toCol) || moveValidator.canPawnCapture(color, fromRow, fromCol, toRow, toCol, false);


    }


}
