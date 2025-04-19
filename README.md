# Chess Game Processor

A Java application for parsing, validating, and analyzing chess games in standard notation, with support for parallel processing.

## Overview

The Chess Game Processor provides a comprehensive framework for handling chess games from PGN (Portable Game Notation) files. The application can parse game notation, validate moves according to chess rules, and visualize game states. It implements a strategic parallel processing architecture to efficiently handle large datasets while maintaining logging integrity.

![Chess Processing Pipeline Visualization](https://via.placeholder.com/800x400?text=Chess+Processing+Pipeline)

## Features

### Game Processing
- **PGN File Parsing**: Robust state machine-based parser for chess game notation
- **Chess Move Parsing**: Support for all standard algebraic notation including:
  - Regular moves (e.g., "e4", "Nf3")
  - Captures (e.g., "Bxe5", "dxe5")
  - Special notation for check (+) and checkmate (#)
  - Castling (O-O, O-O-O)
  - Pawn promotion (e.g., "e8=Q")
  - Disambiguation (e.g., "Rad1", "R1d4")
- **Move Validation**: Complete rule-based validation for:
  - Piece movement patterns
  - Path obstruction
  - Capture legality
  - Special moves (castling, promotion)
- **Board Visualization**: Text-based representation of the chess board

### Parallel Processing Architecture

The application implements a strategic "funnel" architecture that balances parallel processing with controlled evaluation:

```
Multiple Raw      Multiple            Sequential
Material Lines    Assembly Lines      Quality Control
┌─────────┐       ┌─────────┐         ┌─────────┐
│ Parser 1│──┐    │ Moves 1 │──┐      │         │
└─────────┘  │    └─────────┘  │      │         │
┌─────────┐  ├───►┌─────────┐  ├─────►│  Single │
│ Parser 2│──┤    │ Moves 2 │──┤      │Evaluator│
└─────────┘  │    └─────────┘  │      │         │
┌─────────┐  │    ┌─────────┐  │      │         │
│ Parser 3│──┘    │ Moves 3 │──┘      └─────────┘
└─────────┘       └─────────┘
```

This design:
1. **Parallelizes the Heavy Work**: File parsing and move conversion happen concurrently
2. **Maintains Logging Clarity**: Game evaluation occurs sequentially to ensure clean logging
3. **Optimizes Resource Allocation**: Computing power is focused where it delivers maximum returns

## System Architecture

The application follows a clean architecture pattern with distinct layers:

### Model Layer
- `ChessBoard`: Represents the 8x8 chess board and manages piece positions
- `ChessPiece`: Encapsulates chess pieces with properties like type, color, and movement state
- `ChessMove`: Contains complete move data including notation, coordinates, and special flags

### Parser Layer
- `PGNParser`: Extracts game data from PGN files using a state machine approach
- `ChessMovesParser`: Converts algebraic notation to structured move data

### Controller Layer
- `GameMasterController`: Coordinates game evaluation, move validation, and board updates

### Concurrency Layer
- `GameProcessor`: Manages the parallel processing pipeline with thread safety mechanisms

### Logging System
- Thread-safe logging with context awareness
- Hierarchical log structure (application, functional, and file-specific logs)
- Clean separation of parsing and evaluation logs

## Thread Safety Implementation

The application implements several critical patterns to ensure thread safety:

1. **Synchronized Initialization**: Prevents race conditions during logger setup
   ```java
   public static synchronized void init() {
       if (initialized) return;
       // initialization code
       initialized = true;
   }
   ```

2. **Thread-Local Context**: Isolates thread-specific data without locking
   ```java
   private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();
   ```

3. **Controller Per Thread**: Prevents shared state corruption
   ```java
   ThreadLocal<GameMasterController> controllerTL = ThreadLocal.withInitial(() ->
       controllers.get(assigner.getAndIncrement() % nThreads)
   );
   ```

4. **Strategic Parallelism**: Parallel where independent, sequential where coordination matters

## Usage Examples

### Single-Threaded Processing

```java
// Simple single-threaded processing
PGNParser parser = new PGNParser();
parser.parsePGNFile("path/to/games.pgn");
GameMasterController controller = new GameMasterController(new ChessBoard(), true);

for (List<String> move : parser.getGameList()) {
    List<ChessMove> chessMoves = ChessMovesParser.parse(move);
    controller.setChessMoveList(chessMoves);
    controller.Evaluate();
}
```

### Multi-Threaded Processing

```java
// Process multiple files in parallel with 4 worker threads
List<String> files = List.of(
    "games1.pgn",
    "games2.pgn",
    "games3.pgn",
    "games4.pgn"
);
GameProcessor.processGames(files, 4);
```

## Performance Considerations

The parallel processing architecture provides significant performance benefits for:

- **Large PGN Files**: Files containing many games parse much faster
- **Multiple Files**: Processing multiple files simultaneously uses available CPU cores efficiently
- **Move Parsing**: Converting algebraic notation to move data parallelizes well

The sequential evaluation stage ensures:
- **Clear Logging**: No interleaved or corrupted log entries
- **Accurate Game State**: No race conditions or board corruption
- **Deterministic Output**: Consistent results between runs

## Requirements

- Java 17 or higher (uses switch expressions)
- No external dependencies required

## Implementation Details

### The Parsing Pipeline

The parsing process follows these steps:

1. **PGN Reading**: Read files using state machine to handle various formats
2. **Move Extraction**: Extract individual moves from the game text
3. **Notation Parsing**: Convert algebraic notation to structured move data
4. **Move Validation**: Verify each move follows chess rules
5. **Board Updating**: Execute moves on the chess board
6. **Logging**: Record process details for debugging and analysis

### Multithreading Strategy

The multithreaded implementation:

1. Creates a thread pool sized to available cores
2. Assigns one `GameMasterController` per thread
3. Processes files in parallel
4. Parses moves in parallel
5. Evaluates games sequentially for logging clarity
6. Properly cleans up resources when complete

This approach resembles a modern manufacturing facility with specialized zones - multiple processing lines feeding into a controlled quality inspection area.

## Building and Running

### Prerequisites
- Java Development Kit (JDK) 17+

### Building
```bash
javac -d bin *.java
```
(Optional)
### Before running 
delete Games.log and Parser.log  to see cleaner results otherwise new logging entries will be appened onto them.


## Testing

The project includes comprehensive unit tests for all components:
- `ChessBoardTest`: Tests board initialization and reset
- `ChessPieceTest`: Tests piece properties and symbols
- `ChessMovesParserTest`: Tests parsing of various move notations
- `GameMasterControllerTest`: Tests move validation and execution
