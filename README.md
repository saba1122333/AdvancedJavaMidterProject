# Chess Game Processor

A Java application for parsing, validating, and processing chess games in standard notation.

## Overview

This application provides a comprehensive chess game processing system that can:
- Parse PGN (Portable Game Notation) files
- Convert algebraic notation to game moves
- Validate move legality according to chess rules
- Execute and visualize chess games

## Features

- **PGN File Parsing**: Read and parse PGN files with robust error handling
- **Chess Move Parsing**: Support for standard algebraic notation including:
  - Regular moves (e.g., "e4", "Nf3")
  - Captures (e.g., "Bxe5", "dxe5")
  - Special notation for check (+) and checkmate (#)
  - Castling (O-O, O-O-O)
  - Pawn promotion (e.g., "e8=Q")
  - Disambiguation of moves (e.g., "Rad1", "R1d4")
- **Move Validation**: Validates moves based on:
  - Piece movement rules
  - Path obstruction
  - Capture legality
  - Special moves (castling, en passant, promotion)
- **Chess Board Management**: Visual representation of the board state
- **Multiple Game Support**: Process and validate multiple games from a single PGN file



### Model
- `ChessBoard`: Represents the 8x8 chess board and pieces
- `ChessPiece`: Represents individual pieces with properties
- `ChessMove`: Contains move data including notation, coordinates, and special flags

### Parser
- `PGNParser`: Extracts games from PGN files
- `ChessMovesParser`: Converts algebraic notation to structured move data

### Controller
- `GameMasterController`: Manages game flow, validates moves, and updates the board

## Requirements

- Java 17 or higher (supports switch expressions)
- JUnit for running tests

## How to Use

### Running the Application

1. Clone the repository
2. Compile the Java files
3. Run the Main class with a path to your PGN file:

```java
// Example from Main.java
PGNParser parser = new PGNParser();
parser.parsePGNFile("/path/to/your/pgn/file.pgn");

GameMasterController gameMasterController = new GameMasterController(new ChessBoard(), true);

for (List<String> move : parser.getMoves()) {
    List<ChessMove> chessMoves = ChessMovesParser.parse(move);
    gameMasterController.setChessMoveList(chessMoves);
    gameMasterController.Evaluate();
}
```

### Parsing PGN Files

```java
PGNParser parser = new PGNParser();
parser.parsePGNFile("chess_games.pgn");
List<List<String>> games = parser.getMoves(); // Get all games as lists of move strings
```

### Parsing Chess Moves

```java
// Parse a single move
ChessMove move = ChessMovesParser.parseMove("e4", true); // true for white's move

// Parse a list of moves from a game
List<String> moveTexts = Arrays.asList("e4", "e5", "Nf3", "Nc6");
List<ChessMove> moves = ChessMovesParser.parse(moveTexts);
```

### Evaluating Games

```java
ChessBoard board = new ChessBoard();
GameMasterController controller = new GameMasterController(board, false);
controller.setChessMoveList(chessMoves);
controller.Evaluate(); // Validates and executes the moves
```

## Testing

The project includes comprehensive unit tests for all components. Run the tests to verify proper functionality:

- `ChessBoardTest`: Tests board initialization and reset
- `ChessPieceTest`: Tests piece properties and symbols
- `ChessMovesParserTest`: Tests parsing of various move notations
- `GameMasterControllerTest`: Tests move validation and execution

