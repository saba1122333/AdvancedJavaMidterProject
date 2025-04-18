package Bonuse;

import Controller.GameMasterController;
import Model.ChessBoard;
import Model.ChessMove;
import Parser.ChessMovesParser;
import Parser.PGNParser;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameProcessor {

    /**
     * Parses each PGN file in parallel, and in the same thread immediately evaluates
     * every game found using a thread‑specific GameMasterController.
     *
     * @param filePaths  the list of PGN files to process
     * @param nThreads   the size of the thread‑pool (and number of controllers)
     */
    public static void processGames(List<String> filePaths, int nThreads)
            throws InterruptedException {

        // 1) Create a fixed pool of N threads
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);

        // 2) Pre‑create exactly N controllers (one per thread)
        List<GameMasterController> controllers =
                IntStream.range(0, nThreads)
                        .mapToObj(i -> new GameMasterController(new ChessBoard(), true)).toList();

        // 3) Use a ThreadLocal so each physical thread reuses one controller
        AtomicInteger assigner = new AtomicInteger(0);
        ThreadLocal<GameMasterController> controllerTL = ThreadLocal.withInitial(() ->
                // round‑robin assignment on first call in each thread
                controllers.get(assigner.getAndIncrement() % nThreads)
        );

        // 4) Submit one task per file: parse it, then evaluate each game in that same thread
        CountDownLatch done = new CountDownLatch(filePaths.size());
        for (String filePath : filePaths) {
            executor.submit(() -> {
                try {
                    PGNParser parser = new PGNParser();
                    parser.parsePGNFile(filePath);

                    for (List<String> rawGame : parser.getMoves()) {
                        // parse SAN → ChessMove
                        List<ChessMove> gameMoves = ChessMovesParser.parse(rawGame);

                        // grab “this thread’s” controller
                        GameMasterController controller = controllerTL.get();
                        controller.setChessMoveList(gameMoves);
                        controller.Evaluate();
                    }
                } finally {
                    done.countDown();
                }
            });
        }

        // wait for all files to be processed
        done.await();
        executor.shutdown();
    }
}
