package Model;

import java.util.ArrayList;
import java.util.List;

public class GameMaster {
    ChessBoard chessBoard;
    List<ChessMove> chessMoveList;

    public GameMaster(ChessBoard board, List<ChessMove> chessMoveList) {
        this.chessBoard = board;
        this.chessMoveList = chessMoveList;
    }
    // first lets start with simple moves at start checks,pins,captures do not exist

    // we are just assuming there are only simple moves present, no castling,en Passant,promotion,check checkmate or capture

    public void MakeAllMoves() {
        int i = 1;
        ChessMove prev = new ChessMove();
        for (int j = 0; j < chessMoveList.size(); j++) {
            System.out.println(i);
            ChessMove current = chessMoveList.get(j);
            // check here is move was en passent
//            isEnpassant(prev,current);
            i += 1;
            MakeMove(current);
            prev = chessMoveList.get(j);
        }
    }

    public void MakeMove(ChessMove move) {
        var candidateLocations = GetCandidateLocations(move.color, move.pieceType);
        boolean moveMade = false;

        for (int[] candidateLocation : candidateLocations) {
            int fromRow = candidateLocation[0];
            int fromCol = candidateLocation[1];


            if (move.disambiguationFile != null) {
                if (move.fromCol != fromCol) {
                    continue;
                }
            }
            if (move.disambiguationRank != null) {
                if (move.fromRow != fromRow) {
                    continue;
                }

            }
            // add castling logic

            if (move.isPromotion){
                if (canPromote(move.color,move.pieceType,fromRow,fromCol, move.toRow,move.toCol)){
                    // Remove the original pawn
                    chessBoard.board[fromRow][fromCol] = null;

                    // Create the new promoted piece
                    String promotionType = move.promotionPiece != null ? move.promotionPiece : "Queen"; // Default to Queen
                    ChessPiece promotedPiece = new ChessPiece(promotionType, move.color);

                    // Place the new piece on the board
                    chessBoard.board[move.toRow][move.toCol] = promotedPiece;

                    // Record the move source for history
                    move.fromRow = fromRow;
                    move.fromCol = fromCol;

                    System.out.println("Pawn promoted to " + promotionType + " at " +
                            (char)('a' + move.toCol) + (8 - move.toRow));
                    moveMade = true;
                }else {
                    System.out.println("Illegal Promotion attempt: " + move.notation);

                }

            }

            if (move.isCastling) {
                if (CanCastle(move)) {
                    ChessPiece king = chessBoard.board[move.fromRow][move.fromCol];

                    // Calculate rook positions
                    boolean isKingSideCastling = move.toCol == 6;
                    int rookFromCol = isKingSideCastling ? 7 : 0;
                    int rookToCol = isKingSideCastling ? 5 : 3;

                    // Get the rook
                    ChessPiece rook = chessBoard.board[move.fromRow][rookFromCol];

                    // move king
                    chessBoard.board[move.toRow][move.toCol] = king;
                    chessBoard.board[move.fromRow][move.fromCol] = null;

                    king.setMoved();

                    // move rook
                    chessBoard.board[move.toRow][rookToCol] = rook;
                    chessBoard.board[move.fromRow][rookFromCol] = null;
                    rook.setMoved();

                    System.out.println("Executing castling: " + move.notation);
                    moveMade = true;
                } else {
                    System.out.println("Illegal castling attempt: " + move.notation);
                }

            }
            // For captures
            else if (move.isCapture) {
                if (CanCapture(move.color, move.pieceType, fromRow, fromCol, move.toRow, move.toCol, false)) {
                    // Execute the capture
                    System.out.println("Executing capture: " + move.notation);

                    // Store the piece being moved
                    ChessPiece capturingPiece = chessBoard.board[fromRow][fromCol];

                    // If first move, mark as moved
                    if (!capturingPiece.isMoved()) capturingPiece.setMoved();

                    // The captured piece is implicitly removed by being overwritten
                    chessBoard.board[move.toRow][move.toCol] = capturingPiece;
                    chessBoard.board[fromRow][fromCol] = null;

                    // Record the move source
                    move.fromRow = fromRow;
                    move.fromCol = fromCol;

                    moveMade = true;
                    break;
                }
            }
            // For non-captures
            else if (CanMove(move.color, move.pieceType, fromRow, fromCol, move.toRow, move.toCol)) {
                // Execute the regular move
                System.out.println("Executing move: " + move.notation);

                ChessPiece movingPiece = chessBoard.board[fromRow][fromCol];
                if (!movingPiece.isMoved()) movingPiece.setMoved();

                chessBoard.board[move.toRow][move.toCol] = movingPiece;
                chessBoard.board[fromRow][fromCol] = null;

                // Record the move source
                move.fromRow = fromRow;
                move.fromCol = fromCol;

                moveMade = true;
                break;
            }
        }

        if (!moveMade) {
            System.out.println("Warning: No valid move found for " + move.notation);
        }

        chessBoard.printBoard();
    }


    // get candidates coordinates for that move
    public List<int[]> GetCandidateLocations(String color, String type) {
        List<int[]> candidateLocations = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece candidate = chessBoard.board[row][col];
                if (candidate != null && candidate.getType().equals(type) && candidate.getColor().equals(color)) {
                    candidateLocations.add(new int[]{row, col});
                }
            }
        }
        return candidateLocations;
    }


    // then we will check from given position if piece can make that move

    public boolean CanMove(String color, String type, int fromRow, int fromCol, int toRow, int toCol) {
        return switch (type) {
            case "Pawn" -> canPawnMove(color, fromRow, fromCol, toRow, toCol);
            case "King" -> canKingMove(color, fromRow, fromCol, toRow, toCol);
            case "Queen" -> canQueenMove(color, fromRow, fromCol, toRow, toCol);
            case "Bishop" -> canBishopMove(color, fromRow, fromCol, toRow, toCol);
            case "Knight" -> canKnightMove(color, fromRow, fromCol, toRow, toCol);
            case "Rook" -> canRookMove(color, fromRow, fromCol, toRow, toCol);
            default -> // Rook
                    false;
        };

    }

    public boolean CanCapture(String color, String type, int fromRow, int fromCol, int toRow, int toCol, boolean isEnPassant) {
        return switch (type) {
            case "Pawn" -> canPawnCapture(color, fromRow, fromCol, toRow, toCol, isEnPassant);
            case "King" -> canKingMove(color, fromRow, fromCol, toRow, toCol);
            case "Queen" -> canQueenMove(color, fromRow, fromCol, toRow, toCol);
            case "Bishop" -> canBishopMove(color, fromRow, fromCol, toRow, toCol);
            case "Knight" -> canKnightMove(color, fromRow, fromCol, toRow, toCol);
            case "Rook" -> canRookMove(color, fromRow, fromCol, toRow, toCol);
            default -> // Rook
                    false;
        };

    }

    // no captures and enPassant included
    public boolean canPawnMove(String color, int fromRow, int fromCol, int toRow, int toCol) {
        // 1. Verify pawn is moving in the same column (no captures in this method)
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        if (rowDiff == 0 && colDiff == 0) {
            return false;
        }
        if (fromCol != toCol) {
            return false;
        }

        // 2. Set movement direction based on color
        int direction = (color.equals("white")) ? -1 : 1;  // White moves up (-1), Black moves down (+1)

        // 3. Calculate and validate move distance (must be positive in correct direction)
        int moveDistance = (toRow - fromRow) * direction;
        if (moveDistance <= 0) {
            return false;  // Not moving forward in correct direction
        }

        // 4. Verify distance is valid (1 square, or 2 if first move)
        ChessPiece pawn = chessBoard.board[fromRow][fromCol];
        if (moveDistance > 2 || (moveDistance == 2 && pawn.isMoved())) {
            return false;  // Can't move more than 2 squares, or 2 squares after first move
        }

        // 5. Check for clear path
        if (moveDistance == 1) {
            // One-square move: Just check destination
            return chessBoard.board[toRow][toCol] == null;
        } else {
            // Two-square move: Check both intermediate and destination squares
            int intermediateRow = fromRow + direction;
            return chessBoard.board[intermediateRow][toCol] == null &&
                    chessBoard.board[toRow][toCol] == null;
        }
    }

    public boolean canPawnCapture(String color, int fromRow, int fromCol, int toRow, int toCol, boolean isEnPassant) {
        // Ignore the isEnPassant parameter for now - we'll handle standard captures

        // 1. Verify diagonal movement (exactly 1 square diagonally)
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        if (rowDiff != 1 || colDiff != 1) {
            return false;  // Not a diagonal move of exactly one square
        }

        // 2. Verify correct direction based on color
        int direction = color.equals("white") ? -1 : 1;  // White moves up (-1), Black moves down (+1)
        if ((toRow - fromRow) * direction <= 0) {
            return false;  // Moving sideways or backward
        }

        // 3. Verify there's an opponent's piece at the destination
        ChessPiece targetPiece = chessBoard.board[toRow][toCol];
        if (targetPiece == null || targetPiece.getColor().equals(color)) {
            return false;  // No piece to capture or trying to capture own piece
        }

        // All conditions met for a valid pawn capture
        return true;
    }

    public boolean canRookMove(String color, int fromRow, int fromCol, int toRow, int toCol) {
        // 1. Basic validation: can't stay in place and must move in a straight line
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        if (rowDiff == 0 && colDiff == 0) {
            return false;
        }

        // Ensure the move is EITHER horizontal OR vertical (not diagonal)
        if (rowDiff != 0 && colDiff != 0) {
            return false;  // Not a straight line move - rooks can't move diagonally
        }

        // 2. Check horizontal movement (same row)
        if (fromRow == toRow) {
            int startCol = Math.min(fromCol, toCol) + 1;
            int endCol = Math.max(fromCol, toCol);

            // Check each square along the horizontal path
            for (int col = startCol; col < endCol; col++) {
                if (chessBoard.board[fromRow][col] != null) {
                    return false; // Path is blocked
                }
            }
        }
        // 3. Check vertical movement (same column)
        if (fromCol == toCol) {
            int startRow = Math.min(fromRow, toRow) + 1;
            int endRow = Math.max(fromRow, toRow);

            // Check each square along the vertical path
            for (int row = startRow; row < endRow; row++) {
                if (chessBoard.board[row][fromCol] != null) {
                    return false; // Path is blocked
                }
            }

        }

        // 4. Check destination square - can't land on your own piece
        if (chessBoard.board[toRow][toCol] != null &&
                chessBoard.board[toRow][toCol].getColor().equals(color)) {
            return false;
        }

        return true; // All checks passed
    }


    public boolean canBishopMove(String color, int fromRow, int fromCol, int toRow, int toCol) {
        // 1. Basic validation - must be a diagonal move
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        if (rowDiff == 0 && colDiff == 0) {
            return false;
        }
        //  true diagonal
        if (rowDiff != colDiff) {
            return false;
        }
        // 2. Determine diagonal direction of travel
        int rowStep = (toRow > fromRow) ? 1 : -1;
        int colStep = (toCol > fromCol) ? 1 : -1;

        // 3. Check all squares along the diagonal path (excluding start position)
        int row = fromRow + rowStep;
        int col = fromCol + colStep;

        while (row != toRow) {
            if (chessBoard.board[row][col] != null) {
                return false; // Path is blocked
            }
            row += rowStep;
            col += colStep;
        }

        // 4. Check destination square - can't land on your own piece
        if (chessBoard.board[toRow][toCol] != null &&
                chessBoard.board[toRow][toCol].getColor().equals(color)) {
            return false;
        }

        return true; // All checks passed
    }

    public boolean canQueenMove(String color, int fromRow, int fromCol, int toRow, int toCol) {
        return canBishopMove(color, fromRow, fromCol, toRow, toCol) || canRookMove(color, fromRow, fromCol, toRow, toCol);
    }

    public boolean canKnightMove(String color, int fromRow, int fromCol, int toRow, int toCol) {
        // 1. Calculate the distance moved in each direction
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        if (rowDiff == 0 && colDiff == 0) {
            return false;
        }

        // 2. Verify the L-shaped movement pattern
        boolean isValidKnightMove = (rowDiff == 2 && colDiff == 1) ||
                (rowDiff == 1 && colDiff == 2);

        if (!isValidKnightMove) {
            return false;
        }

        // 3. Check destination square - can't land on your own piece
        if (chessBoard.board[toRow][toCol] != null &&
                chessBoard.board[toRow][toCol].getColor().equals(color)) {
            return false;
        }

        return true; // All checks passed
    }

    public boolean canKingMove(String color, int fromRow, int fromCol, int toRow, int toCol) {
        // PART 1: Basic movement validation
        // King can only move one square in any direction
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        if (rowDiff > 1 || colDiff > 1 || (rowDiff == 0 && colDiff == 0)) {
            return false;  // Invalid movement pattern or not moving
        }

        // PART 2: Check destination - can't move to square with friendly piece
        ChessPiece destPiece = chessBoard.board[toRow][toCol];
        if (destPiece != null && destPiece.getColor().equals(color)) {
            return false;  // Can't capture own pieces
        }

        // PART 3: Check if destination square is safe (not under attack)
        return isSquareSafeForKing(color, toRow, toCol);
    }


    /**
     * Determines if castling is legal for the given move.
     */
    private boolean CanCastle(ChessMove move) {
        // Extract the key information
        int kingRow = move.fromRow;
        int kingCol = move.fromCol;
        boolean isKingSideCastling = move.toCol == 6;

        // 1. Verify the king hasn't moved (using the isMoved flag)
        ChessPiece king = chessBoard.board[kingRow][kingCol];
        if (king == null || !king.getType().equals("King") || king.isMoved()) {
            return false;  // King is missing or has moved
        }

        // 2. Identify and check the appropriate rook
        int rookCol = isKingSideCastling ? 7 : 0;  // H-file or A-file
        ChessPiece rook = chessBoard.board[kingRow][rookCol];
        if (rook == null || !rook.getType().equals("Rook") || rook.isMoved()) {
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
            if (!isSquareSafeForKing(move.color, kingRow, col)) {
                return false;  // King would move through or into check
            }
        }

        return true;  // All castling conditions are met
    }


    private boolean canPromote(String color, String type, int fromRow, int fromCol, int toRow, int toCol) {
        // SECURITY CHECK 1: Only pawns can be promoted
        if (!type.equals("Pawn")) {
            System.out.println("Promotion rejected: Only pawns can be promoted");
            return false;
        }

        // SECURITY CHECK 2: Promotion must occur on the correct rank
        int promotionRank = color.equals("white") ? 0 : 7; // Rank 8 for white, rank 1 for black
        if (toRow != promotionRank) {
            System.out.println("Promotion rejected: Not at promotion rank");
            return false;
        }


        // just promotion
        return canPawnMove(color, fromRow, fromCol, toRow, toCol) || canPawnCapture(color, fromRow, fromCol, toRow, toCol, false);


    }


    private boolean isSquareSafeForKing(String color, int row, int col) {
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
                        case "Queen" -> canAttack = canQueenMove(enemyColor, r, c, row, col);
                        case "Rook" -> canAttack = canRookMove(enemyColor, r, c, row, col);
                        case "Bishop" -> canAttack = canBishopMove(enemyColor, r, c, row, col);
                        case "Knight" -> canAttack = canKnightMove(enemyColor, r, c, row, col);
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
}

