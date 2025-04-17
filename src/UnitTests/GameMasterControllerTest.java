
package Controller;
import Model.ChessBoard;
import Model.ChessMove;
import Model.ChessPiece;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Unit tests for the GameMasterController class.
 */
public class GameMasterControllerTest {

    private GameMasterController controller;
    private ChessBoard board = new ChessBoard();

    @Before
    public void setUp() {
        controller = new GameMasterController(board,false);
        board = controller.chessBoard;
    }

    @Test
    public void testGetCandidateLocations() throws Exception {
        // Use reflection to access private method
        java.lang.reflect.Method method = GameMasterController.class.getDeclaredMethod(
                "GetCandidateLocations", String.class, String.class);
        method.setAccessible(true);

        // Test finding white pawns
        @SuppressWarnings("unchecked")
        List<int[]> whitePawns = (List<int[]>) method.invoke(controller, "white", "Pawn");
        assertEquals(8, whitePawns.size());

        // Check one of the pawn locations
        boolean foundPawn = false;
        for (int[] location : whitePawns) {
            if (location[0] == 6 && location[1] == 0) { // a2 pawn
                foundPawn = true;
                break;
            }
        }
        assertTrue("Should find white pawn at a2", foundPawn);

        // Test finding black kings
        @SuppressWarnings("unchecked")
        List<int[]> blackKings = (List<int[]>) method.invoke(controller, "black", "King");
        assertEquals(1, blackKings.size());
        assertEquals(0, blackKings.get(0)[0]); // Row 0
        assertEquals(4, blackKings.get(0)[1]); // Column 4 (e file)
    }

    @Test
    public void testCanPawnMove() throws Exception {
        // Use reflection to access private method
        java.lang.reflect.Method method = GameMasterController.class.getDeclaredMethod(
                "canPawnMove", String.class, int.class, int.class, int.class, int.class);
        method.setAccessible(true);

        // Test white pawn moving forward one square
        boolean canMove = (boolean) method.invoke(controller, "white", 6, 4, 5, 4);
        assertTrue("White pawn should be able to move forward one square", canMove);

        // Test white pawn moving forward two squares on first move
        canMove = (boolean) method.invoke(controller, "white", 6, 4, 4, 4);
        assertTrue("White pawn should be able to move forward two squares on first move", canMove);

        // Test black pawn moving forward one square
        canMove = (boolean) method.invoke(controller, "black", 1, 4, 2, 4);
        assertTrue("Black pawn should be able to move forward one square", canMove);

        // Test pawn moving sideways (invalid)
        canMove = (boolean) method.invoke(controller, "white", 6, 4, 6, 5);
        assertFalse("Pawn should not be able to move sideways", canMove);

        // Test pawn moving backward (invalid)
        canMove = (boolean) method.invoke(controller, "white", 6, 4, 7, 4);
        assertFalse("Pawn should not be able to move backward", canMove);
    }

    @Test
    public void testCanRookMove() throws Exception {
        // Use reflection to access private method
        java.lang.reflect.Method method = GameMasterController.class.getDeclaredMethod(
                "canRookMove", String.class, int.class, int.class, int.class, int.class);
        method.setAccessible(true);

        // Clear the board except for the white rook at a1
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board.board[row][col] = null;
            }
        }
        board.board[7][0] = new ChessPiece("Rook", "white"); // White rook at a1

        // Test rook moving horizontally
        boolean canMove = (boolean) method.invoke(controller, "white", 7, 0, 7, 4);
        assertTrue("Rook should be able to move horizontally", canMove);

        // Test rook moving vertically
        canMove = (boolean) method.invoke(controller, "white", 7, 0, 3, 0);
        assertTrue("Rook should be able to move vertically", canMove);

        // Test rook moving diagonally (invalid)
        canMove = (boolean) method.invoke(controller, "white", 7, 0, 5, 2);
        assertFalse("Rook should not be able to move diagonally", canMove);

        // Test with obstacle in path
        board.board[7][2] = new ChessPiece("Pawn", "white"); // White pawn at c1
        canMove = (boolean) method.invoke(controller, "white", 7, 0, 7, 4);
        assertFalse("Rook should not be able to move through other pieces", canMove);

        // Test with enemy piece at destination (capture)
        board.board[7][2] = null; // Remove the obstacle
        board.board[7][4] = new ChessPiece("Pawn", "black"); // Black pawn at e1
        canMove = (boolean) method.invoke(controller, "white", 7, 0, 7, 4);
        assertTrue("Rook should be able to capture enemy piece", canMove);

        // Test with friendly piece at destination (invalid)
        board.board[7][4] = new ChessPiece("Pawn", "white"); // White pawn at e1
        canMove = (boolean) method.invoke(controller, "white", 7, 0, 7, 4);
        assertFalse("Rook should not be able to capture friendly piece", canMove);
    }

    @Test
    public void testCanBishopMove() throws Exception {
        // Use reflection to access private method
        java.lang.reflect.Method method = GameMasterController.class.getDeclaredMethod(
                "canBishopMove", String.class, int.class, int.class, int.class, int.class);
        method.setAccessible(true);

        // Clear the board except for the white bishop at c1
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board.board[row][col] = null;
            }
        }
        board.board[7][2] = new ChessPiece("Bishop", "white"); // White bishop at c1

        // Test bishop moving diagonally
        boolean canMove = (boolean) method.invoke(controller, "white", 7, 2, 5, 0);
        assertTrue("Bishop should be able to move diagonally", canMove);

        // Test bishop moving horizontally (invalid)
        canMove = (boolean) method.invoke(controller, "white", 7, 2, 7, 5);
        assertFalse("Bishop should not be able to move horizontally", canMove);

        // Test with obstacle in path
        board.board[6][1] = new ChessPiece("Pawn", "white"); // White pawn at b2
        canMove = (boolean) method.invoke(controller, "white", 7, 2, 5, 0);
        assertFalse("Bishop should not be able to move through other pieces", canMove);

        // Test with enemy piece at destination (capture)
        board.board[6][1] = null; // Remove the obstacle
        board.board[5][0] = new ChessPiece("Pawn", "black"); // Black pawn at a3
        canMove = (boolean) method.invoke(controller, "white", 7, 2, 5, 0);
        assertTrue("Bishop should be able to capture enemy piece", canMove);
    }

    @Test
    public void testCanKnightMove() throws Exception {
        // Use reflection to access private method
        java.lang.reflect.Method method = GameMasterController.class.getDeclaredMethod(
                "canKnightMove", String.class, int.class, int.class, int.class, int.class);
        method.setAccessible(true);

        // Clear the board except for the white knight at b1
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board.board[row][col] = null;
            }
        }
        board.board[7][1] = new ChessPiece("Knight", "white"); // White knight at b1

        // Test knight moving in L-shape (2,1)
        boolean canMove = (boolean) method.invoke(controller, "white", 7, 1, 5, 2);
        assertTrue("Knight should be able to move in L-shape (2,1)", canMove);

        // Test knight moving in L-shape (1,2)
        canMove = (boolean) method.invoke(controller, "white", 7, 1, 6, 3);
        assertTrue("Knight should be able to move in L-shape (1,2)", canMove);

        // Test knight moving horizontally (invalid)
        canMove = (boolean) method.invoke(controller, "white", 7, 1, 7, 3);
        assertFalse("Knight should not be able to move horizontally", canMove);

        // Test knight moving diagonally (invalid)
        canMove = (boolean) method.invoke(controller, "white", 7, 1, 6, 2);
        assertFalse("Knight should not be able to move diagonally", canMove);

        // Test with piece in path (knight can jump over)
        board.board[6][1] = new ChessPiece("Pawn", "white"); // White pawn at b2
        canMove = (boolean) method.invoke(controller, "white", 7, 1, 5, 2);
        assertTrue("Knight should be able to jump over other pieces", canMove);

        // Test with friendly piece at destination (invalid)
        board.board[5][2] = new ChessPiece("Pawn", "white"); // White pawn at c3
        canMove = (boolean) method.invoke(controller, "white", 7, 1, 5, 2);
        assertFalse("Knight should not be able to capture friendly piece", canMove);

        // Test with enemy piece at destination (capture)
        board.board[5][2] = new ChessPiece("Pawn", "black"); // Black pawn at c3
        canMove = (boolean) method.invoke(controller, "white", 7, 1, 5, 2);
        assertTrue("Knight should be able to capture enemy piece", canMove);
    }

    @Test
    public void testCanQueenMove() throws Exception {
        // Use reflection to access private method
        java.lang.reflect.Method method = GameMasterController.class.getDeclaredMethod(
                "canQueenMove", String.class, int.class, int.class, int.class, int.class);
        method.setAccessible(true);

        // Clear the board except for the white queen at d1
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board.board[row][col] = null;
            }
        }
        board.board[7][3] = new ChessPiece("Queen", "white"); // White queen at d1

        // Test queen moving horizontally
        boolean canMove = (boolean) method.invoke(controller, "white", 7, 3, 7, 7);
        assertTrue("Queen should be able to move horizontally", canMove);

        // Test queen moving vertically
        canMove = (boolean) method.invoke(controller, "white", 7, 3, 3, 3);
        assertTrue("Queen should be able to move vertically", canMove);

        // Test queen moving diagonally
        canMove = (boolean) method.invoke(controller, "white", 7, 3, 4, 0);
        assertTrue("Queen should be able to move diagonally", canMove);

        // Test queen moving in L-shape (invalid)
        canMove = (boolean) method.invoke(controller, "white", 7, 3, 5, 4);
        assertFalse("Queen should not be able to move in L-shape", canMove);

        // Test with obstacle in path
        board.board[7][5] = new ChessPiece("Pawn", "white"); // White pawn at f1
        canMove = (boolean) method.invoke(controller, "white", 7, 3, 7, 7);
        assertFalse("Queen should not be able to move through other pieces", canMove);
    }

    @Test
    public void testCanKingMove() throws Exception {
        // Use reflection to access private method
        java.lang.reflect.Method method = GameMasterController.class.getDeclaredMethod(
                "canKingMove", String.class, int.class, int.class, int.class, int.class);
        method.setAccessible(true);

        // Clear the board except for the white king at e1
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board.board[row][col] = null;
            }
        }
        board.board[7][4] = new ChessPiece("King", "white"); // White king at e1

        // Test king moving one square horizontally
        boolean canMove = (boolean) method.invoke(controller, "white", 7, 4, 7, 5);
        assertTrue("King should be able to move one square horizontally", canMove);

        // Test king moving one square vertically
        canMove = (boolean) method.invoke(controller, "white", 7, 4, 6, 4);
        assertTrue("King should be able to move one square vertically", canMove);

        // Test king moving one square diagonally
        canMove = (boolean) method.invoke(controller, "white", 7, 4, 6, 5);
        assertTrue("King should be able to move one square diagonally", canMove);

        // Test king moving two squares (invalid)
        canMove = (boolean) method.invoke(controller, "white", 7, 4, 7, 6);
        assertFalse("King should not be able to move two squares", canMove);

        // Test with friendly piece at destination (invalid)
        board.board[6][5] = new ChessPiece("Pawn", "white"); // White pawn at f2
        canMove = (boolean) method.invoke(controller, "white", 7, 4, 6, 5);
        assertFalse("King should not be able to capture friendly piece", canMove);

        // Test with enemy piece at destination (capture)
        board.board[6][5] = new ChessPiece("Pawn", "black"); // Black pawn at f2
        canMove = (boolean) method.invoke(controller, "white", 7, 4, 6, 5);
        assertTrue("King should be able to capture enemy piece", canMove);
    }

    @Test
    public void testCanCastle() throws Exception {
        // Use reflection to access private method
        java.lang.reflect.Method method = GameMasterController.class.getDeclaredMethod(
                "CanCastle", ChessMove.class);
        method.setAccessible(true);

        // Clear the board and set up a position where castling is possible
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board.board[row][col] = null;
            }
        }
        // Set up white king and rooks for castling
        board.board[7][4] = new ChessPiece("King", "white"); // White king at e1
        board.board[7][0] = new ChessPiece("Rook", "white"); // White rook at a1
        board.board[7][7] = new ChessPiece("Rook", "white"); // White rook at h1

        // Create kingside castling move
        ChessMove kingsideCastle = new ChessMove();
        kingsideCastle.color = "white";
        kingsideCastle.pieceType = "King";
        kingsideCastle.fromRow = 7;
        kingsideCastle.fromCol = 4;
        kingsideCastle.toRow = 7;
        kingsideCastle.toCol = 6;
        kingsideCastle.isCastling = true;

        // Test kingside castling
        boolean canCastle = (boolean) method.invoke(controller, kingsideCastle);
        assertTrue("King should be able to castle kingside", canCastle);

        // Create queenside castling move
        ChessMove queensideCastle = new ChessMove();
        queensideCastle.color = "white";
        queensideCastle.pieceType = "King";
        queensideCastle.fromRow = 7;
        queensideCastle.fromCol = 4;
        queensideCastle.toRow = 7;
        queensideCastle.toCol = 2;
        queensideCastle.isCastling = true;

        // Test queenside castling
        canCastle = (boolean) method.invoke(controller, queensideCastle);
        assertTrue("King should be able to castle queenside", canCastle);

        // Test with obstacle in path
        board.board[7][1] = new ChessPiece("Knight", "white"); // White knight at b1
        canCastle = (boolean) method.invoke(controller, queensideCastle);
        assertFalse("King should not be able to castle through other pieces", canCastle);

        // Test when king has moved
        board.board[7][1] = null; // Remove the obstacle
        ChessPiece king = board.board[7][4];
        king.SetMoved(); // Mark king as moved
        canCastle = (boolean) method.invoke(controller, kingsideCastle);
        assertFalse("King should not be able to castle after moving", canCastle);
    }


    @Test
    public void testMakeMoveRegularMove() {
        // Set up a simple pawn move
        ChessMove pawnMove = new ChessMove();
        pawnMove.notation = "e4";
        pawnMove.color = "white";
        pawnMove.pieceType = "Pawn";
        pawnMove.toRow = 4;
        pawnMove.toCol = 4;
        // The fromRow/fromCol are not set because the controller should find the pawn at e2

        List<ChessMove> movesList = new ArrayList<>();
        movesList.add(pawnMove);
        controller.setChessMoveList(movesList);

        // Execute the move
        controller.Evaluate();

        // Verify the pawn has moved
        assertNull(board.board[6][4]); // Original position (e2) should be empty
        assertNotNull(board.board[4][4]); // New position (e4) should have the pawn
        assertEquals("Pawn", board.board[4][4].getType());
        assertEquals("white", board.board[4][4].getColor());
    }
    @Test
    public void testMakeMovePawnCapture() {
        // Set up the board for a pawn capture
        board.board[3][4] = new ChessPiece("Pawn", "black"); // Black pawn at e5
        board.board[4][3] = new ChessPiece("Pawn", "white"); // Black pawn at e5

        // Set up a pawn capture move
        ChessMove captureMove = new ChessMove();
        captureMove.notation = "dxe5";
        captureMove.color = "white";
        captureMove.pieceType = "Pawn";
        captureMove.toRow = 3;
        captureMove.toCol = 4;
        captureMove.isCapture = true;
        captureMove.disambiguationFile = "d";
        captureMove.fromCol = 3; // d file

        List<ChessMove> movesList = new ArrayList<>();
        movesList.add(captureMove);
        controller.setChessMoveList(movesList);

        // Execute the move
        controller.Evaluate();

        // Verify the capture
        assertNull(board.board[4][3]); // Original position should be empty
        assertNotNull(board.board[3][4]); // New position should have the pawn
        assertEquals("Pawn", board.board[3][4].getType());
        assertEquals("white", board.board[3][4].getColor());
    }
}