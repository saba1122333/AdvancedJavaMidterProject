import Bonuse.GameProcessor;
import Controller.GameMasterController;
import Model.ChessBoard;
import Model.ChessMove;
import Parser.ChessMovesParser;
import Parser.PGNParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        List<String> files = List.of(
                "src/testPgns/Philidor.pgn",
                "src/testPgns/customGame.pgn",
                "src/testPgns/Chess Lessons.pgn",
                "src/testPgns/WikiExample.pgn"
        );
        // e.g. use 4 threads/controllers
        GameProcessor.processGames(files, 4);
    }

}