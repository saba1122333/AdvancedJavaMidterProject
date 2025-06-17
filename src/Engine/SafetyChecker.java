package Engine;

import Model.ChessBoard;
import Model.ChessMove;
import Model.ChessPiece;

public class SafetyChecker {
    private ChessBoard chessBoard;
    private BoardAnalyzer boardAnalyzer;
    private MoveValidator moveValidator;

    public SafetyChecker(ChessBoard chessBoard, MoveValidator moveValidator, BoardAnalyzer boardAnalyzer) {
        this.chessBoard = chessBoard;
        this.moveValidator = moveValidator;
        this.boardAnalyzer = boardAnalyzer;
    }

    public boolean IsSquareSafeForKing(String color, int row, int col) {
        String enemyColor = color.equals("white") ? "black" : "white";

        // STEP 1: Check enemy king proximity (kings must stay at least 2 squares apart)
        for (int r = Math.max(0, row - 1); r <= Math.min(7, row + 1); r++) {
            for (int c = Math.max(0, col - 1); c <= Math.min(7, col + 1); c++) {
                ChessPiece piece = chessBoard.board[r][c];
                if (piece != null && piece.getType().equals("King") && piece.getColor().equals(enemyColor)) {
                    return false;  // Enemy king is too close
                }
            }
        }

        // STEP 2: Check for enemy pawns specifically (they control diagonals)
        int pawnRow = color.equals("white") ? row - 1 : row + 1;  // Row where enemy pawns would be to attack
        if (pawnRow >= 0 && pawnRow < 8) {  // Check board boundaries
            // Check left diagonal
            if (col - 1 >= 0) {
                ChessPiece leftPawn = chessBoard.board[pawnRow][col - 1];
                if (leftPawn != null && leftPawn.getType().equals("Pawn") &&
                        leftPawn.getColor().equals(enemyColor)) {
                    return false;  // Square is under attack by enemy pawn
                }
            }
            // Check right diagonal
            if (col + 1 < 8) {
                ChessPiece rightPawn = chessBoard.board[pawnRow][col + 1];
                if (rightPawn != null && rightPawn.getType().equals("Pawn") &&
                        rightPawn.getColor().equals(enemyColor)) {
                    return false;  // Square is under attack by enemy pawn
                }
            }
        }

        // STEP 3: Check all other enemy pieces by scanning the entire board
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                ChessPiece piece = chessBoard.board[r][c];
                if (piece != null && piece.getColor().equals(enemyColor) && !piece.getType().equals("King")) {
                    // Skip pawns as we've already handled their special case
                    if (piece.getType().equals("Pawn")) {
                        continue;
                    }

                    // For all other pieces, check if they can move to this square
                    // We must temporarily remove any piece at the destination for accurate checking
                    ChessPiece originalPiece = chessBoard.board[row][col];
                    chessBoard.board[row][col] = null;  // Temporarily clear the square

                    boolean canAttack = false;
                    switch (piece.getType()) {
                        case "Queen" -> canAttack = moveValidator.canQueenMove(enemyColor, r, c, row, col);
                        case "Rook" -> canAttack = moveValidator.canRookMove(enemyColor, r, c, row, col);
                        case "Bishop" -> canAttack = moveValidator.canBishopMove(enemyColor, r, c, row, col);
                        case "Knight" -> canAttack = moveValidator.canKnightMove(enemyColor, r, c, row, col);
                    }

                    // Restore the original board state
                    chessBoard.board[row][col] = originalPiece;

                    if (canAttack) {
                        return false;  // Square is under attack
                    }
                }
            }
        }

        return true;  // Square is safe for the king
    }


    public boolean IsKingSafe(ChessMove move) {
        boolean moveMade;
        var kingPosition = boardAnalyzer.GetCandidatePositions(move.color, "King");
//        if (kingPosition.isEmpty()) {
//            errorReport.append(move.color).append(" King is not present on the board");
//            moveMade = false;
//        } else if (kingPosition.size() > 2) {
//            errorReport.append("More then one").append(move.color).append(" King is present on the board");
//            moveMade = false;
//        } else {
//            int KingRow = kingPosition.get(0).row();
//            int KingCol = kingPosition.get(0).col();
//            moveMade = IsSquareSafeForKing(move.color, KingRow, KingCol);
//        }
        int KingRow = kingPosition.get(0).row();
        int KingCol = kingPosition.get(0).col();

        return IsSquareSafeForKing(move.color, KingRow, KingCol) && kingPosition.size() == 1;
    }

}
