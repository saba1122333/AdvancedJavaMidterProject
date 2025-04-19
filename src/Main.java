import Controller.GameMasterController;
import Model.ChessBoard;
import Model.ChessMove;
import Parser.ChessMovesParser;
import Parser.PGNParser;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        // welcome to script this is default code ready to be run in  single thread for streamline process from pgn to
        // Parser.log and Games.log file which logs and documents every step of the way how pgn file is parsed transformed and evaluated
        // I will wish myself best of luck hope you will like it...  :)

        PGNParser parser = new PGNParser();
        parser.parsePGNFile("src/testPgns/Philidor.pgn");
        GameMasterController gameMasterController = new GameMasterController(new ChessBoard(), true);
        for (List<String> move : parser.getGameList()) {
            List<ChessMove> chessMoves = ChessMovesParser.parse(move);
            gameMasterController.setChessMoveList(chessMoves);
            gameMasterController.Evaluate();

        }


    }
}