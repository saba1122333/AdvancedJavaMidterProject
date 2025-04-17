package Model;

// A simplified chess board implementation
public class ChessBoard {


    public ChessPiece[][] board;


    public ChessBoard() {
        board = new ChessPiece[8][8];
        SetupBoard();
    }
    public  void ResetBoard(){
        SetupBoard();
    }

    private void SetupBoard() {
        // Initialize empty board (all null)
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = null;
            }
        }
        // Set up black pieces (top of board)
        // Back row (row 0)
        board[0][0] = new ChessPiece("Rook", "black");
        board[0][1] = new ChessPiece("Knight", "black");
        board[0][2] = new ChessPiece("Bishop", "black");
        board[0][3] = new ChessPiece("Queen", "black");
        board[0][4] = new ChessPiece("King", "black");
        board[0][5] = new ChessPiece("Bishop", "black");
        board[0][6] = new ChessPiece("Knight", "black");
        board[0][7] = new ChessPiece("Rook", "black");

        // Black pawns (row 1)
        for (int col = 0; col < 8; col++) {
            board[1][col] = new ChessPiece("Pawn", "black");
        }

        // Set up white pieces (bottom of board)
        // White pawns (row 6)
        for (int col = 0; col < 8; col++) {
            board[6][col] = new ChessPiece("Pawn", "white");
        }

        // Back row (row 7)
        board[7][0] = new ChessPiece("Rook", "white");
        board[7][1] = new ChessPiece("Knight", "white");
        board[7][2] = new ChessPiece("Bishop", "white");
        board[7][3] = new ChessPiece("Queen", "white");
        board[7][4] = new ChessPiece("King", "white");
        board[7][5] = new ChessPiece("Bishop", "white");
        board[7][6] = new ChessPiece("Knight", "white");
        board[7][7] = new ChessPiece("Rook", "white");
    }


    public void PrintBoard() {

        for (int row = 0; row < 8; row++) {
            System.out.print((8-row) + " ");
            for (int col = 0; col < 8; col++) {
                String symbol = board[row][col] != null ? board[row][col].getSymbol() : ". ";
                System.out.print(symbol + " ");
            }
            System.out.println();
        }
        for (char c = 'a'; c <= 'h'; c++) {
            System.out.print("  " + c);
        }

        System.out.println();
        System.out.println();

    }


}