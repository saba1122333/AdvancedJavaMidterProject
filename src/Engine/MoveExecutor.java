package Engine;

import Model.ChessBoard;
import Model.ChessMove;
import Model.ChessPiece;

public class MoveExecutor {
    private ChessBoard chessBoard;

    public MoveExecutor(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    public void ExecuteMoveOrCapture(ChessMove move, int fromRow, int fromCol) {
        // Store the piece being moved
        ChessPiece capturingPiece = chessBoard.board[fromRow][fromCol];

        // If first move, mark as moved
        if (!capturingPiece.IsMoved()) capturingPiece.SetMoved();

        // The captured piece is implicitly removed by being overwritten
        chessBoard.board[move.toRow][move.toCol] = capturingPiece;
        chessBoard.board[fromRow][fromCol] = null;

        // Record the move source
        move.fromRow = fromRow;
        move.fromCol = fromCol;
    }

    public void ExecuteCastling(ChessMove move) {
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

        king.SetMoved();

        // move rook
        chessBoard.board[move.toRow][rookToCol] = rook;
        chessBoard.board[move.fromRow][rookFromCol] = null;
        rook.SetMoved();
    }

    public void ExecuteCheck(ChessMove move, int fromRow, int fromCol) {
        ChessPiece movingPiece = chessBoard.board[fromRow][fromCol];
        ChessPiece capturedPiece = chessBoard.board[move.toRow][move.toCol];

        // 2. Temporarily make the move
        chessBoard.board[move.toRow][move.toCol] = movingPiece;
        chessBoard.board[fromRow][fromCol] = null;
    }

    public void ExecutePromotion(ChessMove move, int fromRow, int fromCol) {
        chessBoard.board[fromRow][fromCol] = null;

        // Create the new promoted piece
        String promotionType = move.promotionPiece != null ? move.promotionPiece : "Queen"; // Default to Queen
        ChessPiece promotedPiece = new ChessPiece(promotionType, move.color);
        // Place the new piece on the board
        chessBoard.board[move.toRow][move.toCol] = promotedPiece;

    }

}