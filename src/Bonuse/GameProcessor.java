package Bonuse;

import Controller.GameMasterController;
import Model.ChessBoard;
import Model.ChessMove;
import Parser.ChessMovesParser;
import Parser.PGNParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GameProcessor {


    public static void processGames(List<String> filePaths, int nThreads)
            throws InterruptedException, ExecutionException {

        // 1) Create a fixed pool of N threads
        ExecutorService parsersPoolService = Executors.newFixedThreadPool(nThreads);
        //
        List<Future<List<List<String>>>> allGames = new ArrayList<>();
        for (String filePath : filePaths) {
            allGames.add(parsersPoolService.submit(() -> {
                PGNParser p = new PGNParser();
                p.parsePGNFile(filePath);
                return p.getGameList();
            }));
        }
        List<List<String>> allFlattenedGames = new ArrayList<>();
        for (Future<List<List<String>>> file : allGames) {
            allFlattenedGames.addAll(file.get());
        }
        List<Future<List<ChessMove>>> chessMoves = new ArrayList<>();
        for (List<String> game : allFlattenedGames) {
            chessMoves.add(
                    parsersPoolService.submit(() ->
                            ChessMovesParser.parse(game)
                    ));
        }
        parsersPoolService.shutdown();

        GameMasterController gm = new GameMasterController(new ChessBoard(), true);
        ExecutorService evaluator = Executors.newSingleThreadExecutor();

        for (Future<List<ChessMove>> chessMove : chessMoves) {
            List<ChessMove> chessMoveList = chessMove.get();
            evaluator.submit(() -> {
                gm.Flush();
                gm.setChessMoveList(chessMoveList);
                gm.Evaluate();
            }).get();
        }

        evaluator.shutdown();

    }
}
