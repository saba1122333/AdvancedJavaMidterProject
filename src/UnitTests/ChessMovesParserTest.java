
/**
 * Unit tests for the ChessPiece class.
 */
package Parser;

import Model.ChessMove;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Unit tests for the ChessMovesParser class.
 */
public class ChessMovesParserTest {

    @Test
    public void testParsePawnMove() {
        ChessMove move = ChessMovesParser.parseMove("e4", true);

        assertEquals("e4", move.notation);
        assertEquals("white", move.color);
        assertEquals("Pawn", move.pieceType);
        assertEquals(4, move.toCol); // e file = 4
        assertEquals(4, move.toRow); // 4th rank = 4
        assertFalse(move.isCapture);
        assertFalse(move.isCheck);
        assertFalse(move.isCheckmate);
        assertFalse(move.isCastling);
        assertFalse(move.isPromotion);
    }

    @Test
    public void testParsePieceMove() {
        ChessMove move = ChessMovesParser.parseMove("Nf3", true);

        assertEquals("Nf3", move.notation);
        assertEquals("white", move.color);
        assertEquals("Knight", move.pieceType);
        assertEquals(5, move.toCol); // f file = 5
        assertEquals(5, move.toRow); // 3rd rank = 5
        assertFalse(move.isCapture);
    }

    @Test
    public void testParseCaptureMove() {
        ChessMove move = ChessMovesParser.parseMove("Bxe5", true);

        assertEquals("Bxe5", move.notation);
        assertEquals("white", move.color);
        assertEquals("Bishop", move.pieceType);
        assertEquals(4, move.toCol); // e file = 4
        assertEquals(3, move.toRow); // 5th rank = 3
        assertTrue(move.isCapture);
    }

    @Test
    public void testParsePawnCapture() {
        ChessMove move = ChessMovesParser.parseMove("dxe5", true);

        assertEquals("dxe5", move.notation);
        assertEquals("white", move.color);
        assertEquals("Pawn", move.pieceType);
        assertEquals(4, move.toCol); // e file = 4
        assertEquals(3, move.toRow); // 5th rank = 3
        assertTrue(move.isCapture);
        assertEquals("d", move.disambiguationFile);
        assertEquals(3, move.fromCol); // d file = 3
    }

    @Test
    public void testParseCheckMove() {
        ChessMove move = ChessMovesParser.parseMove("Qd8+", true);

        assertEquals("Qd8+", move.notation);
        assertEquals("white", move.color);
        assertEquals("Queen", move.pieceType);
        assertEquals(3, move.toCol); // d file = 3
        assertEquals(0, move.toRow); // 8th rank = 0
        assertTrue(move.isCheck);
        assertFalse(move.isCheckmate);
    }

    @Test
    public void testParseCheckmateMove() {
        ChessMove move = ChessMovesParser.parseMove("Qd8#", true);

        assertEquals("Qd8#", move.notation);
        assertEquals("white", move.color);
        assertEquals("Queen", move.pieceType);
        assertEquals(3, move.toCol); // d file = 3
        assertEquals(0, move.toRow); // 8th rank = 0
        assertFalse(move.isCheck);
        assertTrue(move.isCheckmate);
    }

    @Test
    public void testParseCastling() {
        // Kingside castling
        ChessMove kingsideCastle = ChessMovesParser.parseMove("O-O", true);

        assertEquals("O-O", kingsideCastle.notation);
        assertEquals("white", kingsideCastle.color);
        assertEquals("King", kingsideCastle.pieceType);
        assertTrue(kingsideCastle.isCastling);
        assertEquals(7, kingsideCastle.fromRow); // 1st rank for white = 7
        assertEquals(4, kingsideCastle.fromCol); // e file = 4
        assertEquals(7, kingsideCastle.toRow);
        assertEquals(6, kingsideCastle.toCol); // g file = 6

        // Queenside castling
        ChessMove queensideCastle = ChessMovesParser.parseMove("O-O-O", false);

        assertEquals("O-O-O", queensideCastle.notation);
        assertEquals("black", queensideCastle.color);
        assertEquals("King", queensideCastle.pieceType);
        assertTrue(queensideCastle.isCastling);
        assertEquals(0, queensideCastle.fromRow); // 8th rank for black = 0
        assertEquals(4, queensideCastle.fromCol); // e file = 4
        assertEquals(0, queensideCastle.toRow);
        assertEquals(2, queensideCastle.toCol); // c file = 2
    }

    @Test
    public void testParsePromotion() {
        ChessMove move = ChessMovesParser.parseMove("e8=Q", true);

        assertEquals("e8=Q", move.notation);
        assertEquals("white", move.color);
        assertEquals("Pawn", move.pieceType);
        assertEquals(4, move.toCol); // e file = 4
        assertEquals(0, move.toRow); // 8th rank = 0
        assertTrue(move.isPromotion);
        assertEquals("Queen", move.promotionPiece);
    }

    @Test
    public void testParseMultipleMoves() {
        List<String> moveTexts = Arrays.asList("e4", "e5", "Nf3", "Nc6");
        List<ChessMove> moves = ChessMovesParser.parse(moveTexts);

        assertEquals(4, moves.size());

        // First move (white)
        assertEquals("e4", moves.get(0).notation);
        assertEquals("white", moves.get(0).color);

        // Second move (black)
        assertEquals("e5", moves.get(1).notation);
        assertEquals("black", moves.get(1).color);

        // Third move (white)
        assertEquals("Nf3", moves.get(2).notation);
        assertEquals("white", moves.get(2).color);

        // Fourth move (black)
        assertEquals("Nc6", moves.get(3).notation);
        assertEquals("black", moves.get(3).color);
    }

    @Test
    public void testParseDisambiguationMoves() {
        // Rank disambiguation
        ChessMove rankMove = ChessMovesParser.parseMove("R1d4", true);
        assertEquals("R1d4", rankMove.notation);
        assertEquals("Rook", rankMove.pieceType);
        assertEquals(3, rankMove.toCol); // d file = 3
        assertEquals(4, rankMove.toRow); // 4th rank = 4
        assertEquals("1", rankMove.disambiguationRank);
        assertEquals(7, rankMove.fromRow); // 1st rank = 7

        // File disambiguation
        ChessMove fileMove = ChessMovesParser.parseMove("Rad1", true);
        assertEquals("Rad1", fileMove.notation);
        assertEquals("Rook", fileMove.pieceType);
        assertEquals(3, fileMove.toCol); // d file = 3
        assertEquals(7, fileMove.toRow); // 1st rank = 7
        assertEquals("a", fileMove.disambiguationFile);
        assertEquals(0, fileMove.fromCol); // a file = 0
    }
}