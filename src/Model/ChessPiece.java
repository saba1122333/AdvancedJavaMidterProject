package Model;

public class ChessPiece {
    private final String type;    // pawn, knight, bishop, rook, queen, king
    private  final String color;   // white or black
    private boolean isMoved; // for king and rooks (castling) and pawns
    public ChessPiece(String type, String color) {
        this.type = type;
        this.color = color;
        this.isMoved = false;
    }
    public String getType() {
        return type;
    }
    public String getColor() {
        return color;
    }
    public boolean isMoved() {
        return isMoved;
    }
    public void setMoved() {
        isMoved = true;
    }
    public String getSymbol() {
        return switch (this.type) {
            case "Pawn" -> this.color.equals("white") ? "♙ " : "♟ "; // Pawn
            case "King" -> this.color.equals("white") ? "♔ " : "♚ "; // King
            case "Queen" -> this.color.equals("white") ? "♕ " : "♛ "; // Queen
            case "Bishop" -> this.color.equals("white") ? "♗ " : "♝ "; // Bishop
            case "Knight" -> this.color.equals("white") ? "♘ " : "♞ "; // Knight
            case "Rook" -> this.color.equals("white") ? "♖ " : "♜ ";
            default -> // Rook
                    ". ";
        };
    }
}
