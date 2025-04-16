package Parser;

import Model.ChessMove;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChessMovesParser {

   static public List<ChessMove> parse(List<String> moves) {
        boolean isWhiteToMove = true;
        List<ChessMove> chessMoves = new ArrayList<>();

        for (String moveText : moves) {
            if (!moveText.isEmpty()) {
                ChessMove move = parseMove(moveText, isWhiteToMove);
                // After basic parsing, resolve the source coordinates if they're ambiguous
                chessMoves.add(move);
                isWhiteToMove = !isWhiteToMove;
            }
        }
        return chessMoves;
    }

    public static ChessMove parseMove(String moveText, boolean isWhiteToMove) {
        ChessMove move = new ChessMove();
        move.notation = moveText;
        move.color = isWhiteToMove ? "white" : "black";

        // Handle castling
        if (moveText.matches("O-O(-O)?|0-0(-0)?")) {
            return parseCastling(moveText, isWhiteToMove);
        }

        // Extract check/checkmate indicators
        move.isCheck = moveText.contains("+") ;
        move.isCheckmate = moveText.contains("#") ;
        String cleanMove = moveText.replaceAll("[+#]", "");

        // Parse regular moves using regex
        // Pattern matches: 1 [piece] 2 [from file] 3 [from rank] 4 [capture] 5 [destination] 6[promotion 7 (group 6 is full "=Q", group 7 is just "Q")]
        Pattern pattern = Pattern.compile("([KQRBN])?([a-h])?([1-8])?(x)?([a-h][1-8])(=([QRBN]))?");
        Matcher matcher = pattern.matcher(cleanMove);

        if (matcher.matches()) {



            // Extract piece type
            String pieceChar = matcher.group(1);
            move.pieceType = pieceChar == null ? "Pawn" : convertPieceCode(pieceChar);

            // Extract capture flag
            move.isCapture = matcher.group(4) != null;






            // Extract destination square
            String destination = matcher.group(5);
            move.toCol = fileToColumn(destination.charAt(0));
            move.toRow = rankToRow(destination.charAt(1));

            // Extract promotion
            move.isPromotion = matcher.group(6) != null;
            move.promotionPiece = move.isPromotion ? convertPieceCode(String.valueOf(matcher.group(7))) :null;

            // Extract origin hints (for disambiguation)
            String disambiguationFile = matcher.group(2);
            String disambiguationRank = matcher.group(3);

            // Set known origin coordinates or mark for resolution
            move.fromCol = disambiguationFile != null ? fileToColumn(disambiguationFile.charAt(0)) : -1;
            move.fromRow = disambiguationRank != null ? rankToRow(disambiguationRank.charAt(0)) : -1;

            // Special handling for pawn captures that always specify the file
            if (move.pieceType.equals("Pawn") && move.isCapture && disambiguationFile != null) {
                move.fromCol = fileToColumn(disambiguationFile.charAt(0));
                // Row will be resolved later using the board state
            }
            move.disambiguationFile = disambiguationFile;
            move.disambiguationRank = disambiguationRank;
        }

        return move;
    }

    private static ChessMove parseCastling(String moveText, boolean isWhiteToMove) {
        ChessMove move = new ChessMove();
        move.notation = moveText;
        move.color = isWhiteToMove ? "white" : "black";
        move.pieceType = "King";
        move.isCastling = true;

        // Set source and destination coordinates based on castling type
        boolean isQueenside = moveText.contains("-O-O") || moveText.contains("-0-0");
        int kingRow = isWhiteToMove ? 7 : 0;  // 7 for white (bottom), 0 for black (top)

        move.fromRow = kingRow;
        move.fromCol = 4;  // King always starts at e1 (white) or e8 (black)
        move.toRow = kingRow;
        move.toCol = isQueenside ? 2 : 6;  // c-file for queenside, g-file for kingside

        return move;
    }

    // Helper methods to convert between chess notation and array indices
    private static int fileToColumn(char file) {
        return file - 'a';  // 'a' -> 0, 'b' -> 1, etc.
    }

    private static int rankToRow(char rank) {
        return 8 - Character.getNumericValue(rank);  // '1' -> 7, '2' -> 6, etc.
    }

    private static String convertPieceCode(String code) {
        return switch (code) {
            case "K" -> "King";
            case "Q" -> "Queen";
            case "R" -> "Rook";
            case "B" -> "Bishop";
            case "N" -> "Knight";
            default -> "Pawn";
        };
    }
}