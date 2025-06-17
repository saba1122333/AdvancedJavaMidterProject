package UnitTests;

import Records.Position;
import org.junit.Test;

import static org.junit.Assert.*;

public class PositionTest {

    @Test
    public void testValidPositionCreation() {
        Position pos = new Position(3, 4);
        assertEquals(3, pos.row());
        assertEquals(4, pos.col());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRowNegative() {
        new Position(-1, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRowTooLarge() {
        new Position(8, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidColNegative() {
        new Position(3, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidColTooLarge() {
        new Position(3, 8);
    }

    @Test
    public void testFromNotationValid() {
        // Test corner squares
        Position a1 = Position.fromNotation("a1");
        assertEquals(7, a1.row()); // rank 1 = row 7
        assertEquals(0, a1.col()); // file a = col 0

        Position h8 = Position.fromNotation("h8");
        assertEquals(0, h8.row()); // rank 8 = row 0
        assertEquals(7, h8.col()); // file h = col 7

        // Test middle squares
        Position e4 = Position.fromNotation("e4");
        assertEquals(4, e4.row()); // rank 4 = row 4
        assertEquals(4, e4.col()); // file e = col 4

        Position d5 = Position.fromNotation("d5");
        assertEquals(3, d5.row()); // rank 5 = row 3
        assertEquals(3, d5.col()); // file d = col 3
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromNotationInvalidLength() {
        Position.fromNotation("e44");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromNotationEmptyString() {
        Position.fromNotation("");
    }

    @Test
    public void testToNotation() {
        assertEquals("a1", new Position(7, 0).toNotation());
        assertEquals("h8", new Position(0, 7).toNotation());
        assertEquals("e4", new Position(4, 4).toNotation());
        assertEquals("d5", new Position(3, 3).toNotation());
        assertEquals("a8", new Position(0, 0).toNotation());
        assertEquals("h1", new Position(7, 7).toNotation());
    }

    @Test
    public void testRoundTripConversion() {
        String[] testSquares = {"a1", "a8", "h1", "h8", "e4", "d5", "c3", "f6"};

        for (String square : testSquares) {
            Position pos = Position.fromNotation(square);
            String converted = pos.toNotation();
            assertEquals("Round trip failed for: " + square, square, converted);
        }
    }

    @Test
    public void testIsValid() {
        // Valid positions
        assertTrue(new Position(0, 0).isValid());
        assertTrue(new Position(7, 7).isValid());
        assertTrue(new Position(3, 4).isValid());

        // All valid positions should pass
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                assertTrue("Position (" + row + "," + col + ") should be valid",
                        new Position(row, col).isValid());
            }
        }
    }

    @Test
    public void testEquality() {
        Position pos1 = new Position(3, 4);
        Position pos2 = new Position(3, 4);
        Position pos3 = new Position(3, 5);

        assertEquals(pos1, pos2);
        assertNotEquals(pos1, pos3);
        assertEquals(pos1.hashCode(), pos2.hashCode());
    }

    @Test
    public void testToString() {
        Position pos = new Position(3, 4);
        String str = pos.toString();
        assertTrue("toString should contain row", str.contains("3"));
        assertTrue("toString should contain col", str.contains("4"));
    }

    @Test
    public void testAllChessSquares() {
        // Test all 64 squares can be created and converted
        for (char file = 'a'; file <= 'h'; file++) {
            for (char rank = '1'; rank <= '8'; rank++) {
                String notation = "" + file + rank;
                Position pos = Position.fromNotation(notation);

                // Verify position is within bounds
                assertTrue("Row out of bounds for " + notation, pos.row() >= 0 && pos.row() <= 7);
                assertTrue("Col out of bounds for " + notation, pos.col() >= 0 && pos.col() <= 7);

                // Verify round trip
                assertEquals("Round trip failed for " + notation, notation, pos.toNotation());
            }
        }
    }
}