package Engine;

import Model.ChessBoard;
import Model.ChessPiece;
import Records.Position;

import java.util.ArrayList;
import java.util.List;

public class BoardAnalyzer {

    private ChessBoard chessBoard;

    public BoardAnalyzer(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    public List<Position> GetCandidatePositions(String color, String type) {
        List<Position> candidateLocations = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece candidate = chessBoard.board[row][col];
                if (candidate != null && candidate.getType().equals(type) && candidate.getColor().equals(color)) {
                    candidateLocations.add(new Position(row, col));
                }
            }
        }
        return candidateLocations;
    }
}
