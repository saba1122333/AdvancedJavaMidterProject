package UnitTests;

import Model.ChessBoard;
import Model.ChessPiece;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the ChessBoard class.
 */
public class ChessBoardTest {
    
    private ChessBoard chessBoard;
    
    @Before
    public void setUp() {
        chessBoard = new ChessBoard();
    }
    
    @Test
    public void testInitialBoardSetup() {
        // Test that black pieces are in correct positions (row 0 - back row)
        assertEquals("Rook", chessBoard.board[0][0].getType());
        assertEquals("black", chessBoard.board[0][0].getColor());
        
        assertEquals("Knight", chessBoard.board[0][1].getType());
        assertEquals("black", chessBoard.board[0][1].getColor());
        
        assertEquals("Bishop", chessBoard.board[0][2].getType());
        assertEquals("black", chessBoard.board[0][2].getColor());
        
        assertEquals("Queen", chessBoard.board[0][3].getType());
        assertEquals("black", chessBoard.board[0][3].getColor());
        
        assertEquals("King", chessBoard.board[0][4].getType());
        assertEquals("black", chessBoard.board[0][4].getColor());
        
        // Test that black pawns are in correct positions (row 1)
        for (int col = 0; col < 8; col++) {
            assertEquals("Pawn", chessBoard.board[1][col].getType());
            assertEquals("black", chessBoard.board[1][col].getColor());
        }
        
        // Test middle rows are empty
        for (int row = 2; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                assertNull(chessBoard.board[row][col]);
            }
        }
        
        // Test that white pawns are in correct positions (row 6)
        for (int col = 0; col < 8; col++) {
            assertEquals("Pawn", chessBoard.board[6][col].getType());
            assertEquals("white", chessBoard.board[6][col].getColor());
        }
        
        // Test that white pieces are in correct positions (row 7 - back row)
        assertEquals("Rook", chessBoard.board[7][0].getType());
        assertEquals("white", chessBoard.board[7][0].getColor());
        
        assertEquals("Knight", chessBoard.board[7][1].getType());
        assertEquals("white", chessBoard.board[7][1].getColor());
        
        assertEquals("Bishop", chessBoard.board[7][2].getType());
        assertEquals("white", chessBoard.board[7][2].getColor());
        
        assertEquals("Queen", chessBoard.board[7][3].getType());
        assertEquals("white", chessBoard.board[7][3].getColor());
        
        assertEquals("King", chessBoard.board[7][4].getType());
        assertEquals("white", chessBoard.board[7][4].getColor());
    }
    
    @Test
    public void testResetBoard() {
        // Modify the board
        chessBoard.board[3][3] = new ChessPiece("Queen", "white");
        chessBoard.board[0][0] = null;
        
        // Reset the board
        chessBoard.ResetBoard();
        
        // Verify board is back to initial state
        assertEquals("Rook", chessBoard.board[0][0].getType());
        assertEquals("black", chessBoard.board[0][0].getColor());
        assertNull(chessBoard.board[3][3]);
    }
}

