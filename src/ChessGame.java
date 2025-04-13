import java.util.List;
import java.util.Map;

public class ChessGame {
    Map<String, String> headers;
    List<String> moves;

    public ChessGame(Map<String, String> headers, List<String> moves) {
        this.headers = headers;
        this.moves = moves;
    }
}
