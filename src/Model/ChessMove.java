package Model;
public class ChessMove {
    // Player information
    public String color;        // "white" or "black"

    // Move notation
    public String notation;      // The original SAN string (e.g., "e4", "Nxf3+")

    // Piece information
    public String pieceType;     // "King", "Queen", "Rook", "Bishop", "Knight", "Pawn"

    // Board coordinates
    public int fromRow;          // Starting row (0-7)
    public int fromCol;          // Starting column (0-7)
    public int toRow;            // Destination row (0-7)
    public int toCol;            // Destination column (0-7)

    // Special move flags
    public boolean isCapture;    // Whether this move captures a piece
    public boolean isCheck;      // Whether this move gives check
    public boolean isCheckmate;  // Whether this move gives checkmate
    public boolean isCastling;   // Whether this is a castling move
    public boolean isPromotion;  // Whether this is a pawn promotion
    public String promotionPiece; // If promoting, what piece type ("Queen", "Rook", etc.)

    // For disambiguation
    public String disambiguationFile;  // File used for disambiguation (if any)
    public String disambiguationRank;  // Rank used for disambiguation (if any)

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Basic move information
        sb.append("ChessMove: ").append(notation).append("\n");
        sb.append("Player: ").append(color).append("\n");

        // Piece information
        sb.append("Piece: ").append(pieceType).append("\n");

        sb.append("destination square with Mapping: ").append(toRow).append(toCol).append("\n");



        if (disambiguationFile != null || disambiguationRank != null) {
            sb.append(" (with disambiguation: ");
            if (disambiguationFile != null) sb.append("file ").append(disambiguationFile).append(" (Column ").append(fromCol).append(" )");
            if (disambiguationFile != null && disambiguationRank != null) sb.append(", ");
            if (disambiguationRank != null) sb.append("rank ").append(disambiguationRank).append(" (Row ").append(fromRow).append(" )");
            sb.append(")");
        }
        sb.append("\n");

//        sb.append("To: ").append(toFile).append(toRank).append("\n");

        // Special move flags
        if (isCapture || isCheck || isCheckmate || isCastling  || isPromotion) {
            sb.append("Special move flags:\n");
            if (isCapture) sb.append("  - Capture\n");
            if (isCheck) sb.append("  - Check\n");
            if (isCheckmate) sb.append("  - Checkmate\n");
            if (isCastling) sb.append("  - Castling\n");
            if (isPromotion) sb.append("  - Promotion to ").append(promotionPiece).append("\n");
        }

        return sb.toString();
    }
}