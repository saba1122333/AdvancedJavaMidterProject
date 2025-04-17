package UnitTests;

import Model.ChessPiece;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChessPieceTest {

    @Test
    public void testPieceCreation() {
        ChessPiece pawn = new ChessPiece("Pawn", "white");
        assertEquals("Pawn", pawn.getType());
        assertEquals("white", pawn.getColor());
        assertFalse(pawn.IsMoved());
    }

    @Test
    public void testSetMoved() {
        ChessPiece king = new ChessPiece("King", "black");
        assertFalse(king.IsMoved());

        king.SetMoved();
        assertTrue(king.IsMoved());
    }

    @Test
    public void testGetSymbol() {
        // Test white pieces
        assertEquals("♙ ", new ChessPiece("Pawn", "white").getSymbol());
        assertEquals("♔ ", new ChessPiece("King", "white").getSymbol());
        assertEquals("♕ ", new ChessPiece("Queen", "white").getSymbol());
        assertEquals("♗ ", new ChessPiece("Bishop", "white").getSymbol());
        assertEquals("♘ ", new ChessPiece("Knight", "white").getSymbol());
        assertEquals("♖ ", new ChessPiece("Rook", "white").getSymbol());

        // Test black pieces
        assertEquals("♟ ", new ChessPiece("Pawn", "black").getSymbol());
        assertEquals("♚ ", new ChessPiece("King", "black").getSymbol());
        assertEquals("♛ ", new ChessPiece("Queen", "black").getSymbol());
        assertEquals("♝ ", new ChessPiece("Bishop", "black").getSymbol());
        assertEquals("♞ ", new ChessPiece("Knight", "black").getSymbol());
        assertEquals("♜ ", new ChessPiece("Rook", "black").getSymbol());

        // Test invalid piece type
        assertEquals(". ", new ChessPiece("Unknown", "white").getSymbol());
    }
}
