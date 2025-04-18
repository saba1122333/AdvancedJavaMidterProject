import Controller.GameMasterController;
import Model.ChessBoard;
import Model.ChessMove;
import Parser.ChessMovesParser;
import Parser.PGNParser;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        PGNParser parser = new PGNParser();
        parser.parsePGNFile("/Users/sabatchumburidze/Desktop/Advanced Java/AdvancedJavaMIdtermProject/AdvancedJavaMIdtermProject/src/testPgns/Philidor.pgn");

        GameMasterController gameMasterController = new GameMasterController(new ChessBoard(),true);

        int GameCounter = 1;
        for (List<String> move : parser.getMoves()) {
            List<ChessMove> chessMoves = ChessMovesParser.parse(move);
            System.out.println("Game" + GameCounter);
            gameMasterController.setChessMoveList(chessMoves);
            gameMasterController.Evaluate();
            GameCounter += 1;

        }


    }
}