

package Parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A robust PGN file parser that uses a state machine approach
 * to accurately parse chess games regardless of formatting.
 */
public class PGNParser {
    // Collections to store parsed games
    private List<List<String>> moves = new ArrayList<>();

    // State machine states
    private enum ParserState {
        BETWEEN_GAMES,  // Between games or at start of file
        IN_HEADERS,     // Currently reading header tags
        IN_MOVES        // Currently reading moves
    }

    /**
     * Processes a PGN file and extracts all games
     *
     * @param fileName Path to the PGN file
     */
    public void parsePGNFile(String fileName) {
        // Clear any previous data

        moves.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            ParserState state = ParserState.BETWEEN_GAMES;
            StringBuilder currentHeaders = new StringBuilder();
            StringBuilder currentMoves = new StringBuilder();

            // Patterns to detect structural elements
            Pattern headerPattern = Pattern.compile("\\[(\\w+)\\s+\"(.*)\"\\]");
            Pattern moveNumberPattern = Pattern.compile("^\\d+\\."); // Starts with digits followed by dot
            Pattern resultPattern = Pattern.compile("(1-0|0-1|1/2-1/2|\\*)\\s*$");

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip empty lines but don't change state based on them
                if (line.isEmpty()) {
                    continue;
                }

                // Check if this line is a header tag
                Matcher headerMatcher = headerPattern.matcher(line);
                if (headerMatcher.matches()) {
                    // Found a header tag

                    if (state == ParserState.IN_MOVES) {
                        // If we were in moves and found a header, this is a new game
                        // Process the completed game first
                        processMoves(currentMoves);
                        currentMoves = new StringBuilder();
                        currentHeaders = new StringBuilder();
                    }

                    // Add header to current collection

                    state = ParserState.IN_HEADERS;
                    continue;
                }

                // Detect move section by looking for move numbers or algebraic notation
                Matcher moveNumberMatcher = moveNumberPattern.matcher(line);
                if (moveNumberMatcher.find() || containsChessNotation(line)) {
                    state = ParserState.IN_MOVES;
                    currentMoves.append(line).append(" ");

                    // Check if this line contains a game result indicator
                    Matcher resultMatcher = resultPattern.matcher(line);
                    if (resultMatcher.find()) {
                        // We've reached the end of a game

                        processMoves(currentMoves);
                        currentMoves = new StringBuilder();
                        state = ParserState.BETWEEN_GAMES;
                    }
                    continue;
                }

                // If we're already in a move section, continue adding lines
                if (state == ParserState.IN_MOVES) {
                    currentMoves.append(line).append(" ");

                    // Check for end of game
                    Matcher resultMatcher = resultPattern.matcher(line);
                    if (resultMatcher.find()) {
                        processMoves(currentMoves);
                        currentMoves = new StringBuilder();
                        state = ParserState.BETWEEN_GAMES;
                    }
                }
            }

            // Process the final game if there is one in progress
            if (currentMoves.length() > 0) {
                processMoves(currentMoves);
            }

        } catch (IOException e) {
            System.err.println("Error reading PGN file: " + e.getMessage());
            e.printStackTrace();
        }





    }

    /**
     * Check if a line contains chess algebraic notation
     * This is a simple heuristic detector
     */
    private boolean containsChessNotation(String line) {
        // Look for common patterns in algebraic notation
        Pattern piecePattern = Pattern.compile("[KQRBN][a-h][1-8]");
        Pattern pawnMovePattern = Pattern.compile("[a-h][1-8]");
        Pattern capturePattern = Pattern.compile("[KQRBNa-h]x[a-h][1-8]");
        Pattern castlePattern = Pattern.compile("O-O(-O)?");

        return piecePattern.matcher(line).find() ||
                pawnMovePattern.matcher(line).find() ||
                capturePattern.matcher(line).find() ||
                castlePattern.matcher(line).find();
    }

    /**
     * Process headers and add to the headers collection
     * <p>
     * <p>
     * /**
     * Process moves and add to the moves collection
     * //
     */


    private void processMoves(StringBuilder moveSection) {
        String movesText = moveSection.toString().trim();

        // Remove game result indicators
        movesText = movesText.replaceAll("1-0|0-1|1/2-1/2|\\*", "");

        // Remove comments in curly braces and parentheses
        movesText = movesText.replaceAll("\\{[^}]*\\}", "");
        movesText = movesText.replaceAll("\\([^)]*\\)", "");

        // Split the move text by move numbers using regex
        // This captures the move number as group 1 and the move content as group 2
        Pattern moveNumberPattern = Pattern.compile("(\\d+\\.)(.*?)(?=\\d+\\.|$)");
        Matcher matcher = moveNumberPattern.matcher(movesText);

        List<String[]> potentialMoves = new ArrayList<>();
        List<String> moveList = new ArrayList<>();
        while (matcher.find()) {
            // Get the content after the move number (group 2)
            String moveContent = matcher.group(2).trim();
            // Split this content by whitespace to get individual moves
            potentialMoves.add(moveContent.split("\\s+"));
            // Process each move in the pair
        }
        // make sure all moves contain exactly 2
        if (potentialMoves.isEmpty()) {
            return;
        }
        for (int i = 0; i < potentialMoves.size(); i++) {
            String[] move = potentialMoves.get(i);
            // Regular move pairs (all except the last one)
            if (i != potentialMoves.size() - 1) {
                // Must have exactly 2 moves (white and black)
                if (move.length != 2) {
                    System.out.print("moveSection contains inconsistencies. at " + (i + 1) +". Expected 2 moves" + " found " + move.length + ": ");


                    for (String s : move) {
                        System.out.print(s + " ");
                    }
                    System.out.println();
                    System.out.print("Game: ");
                    System.out.println(moveSection);
                    System.out.println("will be Omitted");
                    return;
                }
            }
            // Special case: the last move pair
            else {
                // Can have either 1 move (white only) or 2 moves (white and black)
                if (move.length != 1 && move.length != 2) {
                    System.out.print("Last move contains inconsistencies. Expected 1 or 2 moves, found " + move.length + ": ");
                    for (String s : move) {
                        System.out.print(s + " ");
                    }
                    System.out.println();
                    System.out.print("Game: ");
                    System.out.println(moveSection);
                    System.out.println("will be Omitted");
                    return;
                }
            }

            for (String s : move) {
                moveList.add(s.trim());
            }


        }
        System.out.println("Game was successfully parsed");

        moves.add(moveList);

    }


    /**
     * Get the parsed moves for all games
     */
    public List<List<String>> getMoves() {
        return moves;
    }
}