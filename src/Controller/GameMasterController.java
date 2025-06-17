
package Controller;

import Engine.*;
import Model.ChessBoard;
import Model.ChessMove;
import Records.Position;

import java.util.List;

public class GameMasterController {
    ChessBoard chessBoard;
    final boolean forMultipleGames;
    List<ChessMove> chessMoveList;
    StringBuilder errorReport;

    // Extracted components
    private MoveValidator moveValidator;
    private SafetyChecker safetyChecker;
    private SpecialMoveHandler specialMoveHandler;
    private BoardAnalyzer boardAnalyzer;

    private MoveExecutor moveExecutor;

    public GameMasterController(ChessBoard chessBoard, boolean forMultipleGames) {
        this.chessBoard = chessBoard;
        this.forMultipleGames = forMultipleGames;
        // Initialize components
        this.moveValidator = new MoveValidator(chessBoard);
        this.boardAnalyzer = new BoardAnalyzer(chessBoard);
        this.safetyChecker = new SafetyChecker(chessBoard, moveValidator, boardAnalyzer);
        System.out.println("Creating BoardAnalyzer...");
        this.specialMoveHandler = new SpecialMoveHandler(chessBoard, moveValidator, safetyChecker, boardAnalyzer);
        this.moveExecutor = new MoveExecutor(chessBoard);
        this.moveValidator.setSafetyChecker(safetyChecker);

    }

    public List<ChessMove> getChessMoveList() {
        return chessMoveList;
    }

    public void Flush() {
        chessMoveList = null;
    }


    public void setChessMoveList(List<ChessMove> chessMoveList) {
        this.chessMoveList = chessMoveList;
    }

    public void Evaluate() {
        int i = 1;
        for (ChessMove move : chessMoveList) {

            Util.GameLogger.info("Starting Evaluation");
            Util.GameLogger.info("Move: " + (i) + " " + move.color + " " + move.notation);


            i += 1;
            MakeMove(move);
            if (!errorReport.isEmpty()) {
                break;
            }
        }
        if (!errorReport.isEmpty()) {
            Util.GameLogger.warning("Stopping Evaluation");
            Util.GameLogger.warning("Game was  played  with  Violations ");
            Util.GameLogger.warning(errorReport.toString());

        } else {
            Util.GameLogger.info("Game was played  with no Violations ");
        }
        Util.GameLogger.warning("Ending Evaluation");
        Util.GameLogger.info("Visualizing Last Position ...");

        chessBoard.PrintBoard();
        if (forMultipleGames) {
            Util.GameLogger.info("Resetting board");
            chessBoard.ResetBoard();
        }
        Util.GameLogger.info("Evaluation has Ended");


    }

    private void MakeMove(ChessMove move) {
        var candidatePositions = boardAnalyzer.GetCandidatePositions(move.color, move.pieceType);
        errorReport = candidatePositions.isEmpty() ? new StringBuilder("No " + move.color + " Candidate " + " found on the board For: " + move.notation) : new StringBuilder();


        for (Position candidateLocation : candidatePositions) {
            int fromRow = candidateLocation.row();
            int fromCol = candidateLocation.col();

            if (move.disambiguationFile != null) {
                if (move.fromCol != fromCol) {
                    continue;
                }
            }
            if (move.disambiguationRank != null) {
                if (move.fromRow != fromRow) {
                    continue;
                }

            }

            if (move.isPromotion) {
                // add castling logic
                if (specialMoveHandler.CanPromote(move.color, move.pieceType, fromRow, fromCol, move.toRow, move.toCol)) {
                    // Remove the original pawn
                    Util.GameLogger.info("Executing Promotion: " + move.notation);

                    moveExecutor.ExecutePromotion(move, fromRow, fromCol);
                }
            }

            if (move.isCheck) {
                if (specialMoveHandler.CanCheck(move.color, move.pieceType, fromRow, fromCol, move.toRow, move.toCol, move.isPromotion)) {
                    Util.GameLogger.info("Executing Check: " + move.notation);
                    if (!move.isPromotion) {
                        moveExecutor.ExecuteCheck(move, fromRow, fromCol);

                    }
                }

            }

            if (move.isCastling) {
                if (specialMoveHandler.CanCastle(move)) {
                    Util.GameLogger.info("Executing castling: " + move.notation);
                    moveExecutor.ExecuteCastling(move);
                } else {
                    errorReport.append(move.color).append(" Castling not allowed ");

                }

            }
            // For captures
            else if (move.isCapture) {
                if (moveValidator.CanCapture(move.color, move.pieceType, fromRow, fromCol, move.toRow, move.toCol, false)) {
                    // Execute the capture
                    Util.GameLogger.info("Executing capture: " + move.notation);
                    moveExecutor.ExecuteMoveOrCapture(move, fromRow, fromCol);
                }
            }
            // For non-captures
            else if (moveValidator.CanMove(move.color, move.pieceType, fromRow, fromCol, move.toRow, move.toCol)) {
                // Execute the regular move
                Util.GameLogger.info("Executing move: " + move.notation);

                moveExecutor.ExecuteMoveOrCapture(move, fromRow, fromCol);

            }
        }


        if (!safetyChecker.IsKingSafe(move)) {
            if (errorReport.isEmpty()) {
                // Set a generic error if no specific error was recorded
                errorReport.append("Invalid move ").append(move.notation).append(" for ").append(move.color).append(" ").append(move.pieceType).append("\n");

                // Add more context for specific move types
                if (move.isCapture) {
                    errorReport.append(". No valid capture found at target square");
                } else if (move.isPromotion) {
                    errorReport.append(". Cannot promote at the specified position");
                } else if (move.isCastling) {
                    errorReport.append(". Castling not possible in current position");
                } else if (move.isCheck) {
                    errorReport.append(". Move would not result in check");
                } else {
                    errorReport.append(". No legal path to destination");
                }
                errorReport.append("\n");
            }
        }


    }

}

