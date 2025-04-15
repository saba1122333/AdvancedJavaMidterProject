package Parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PGNParser {
    // Collections to store parsed games
    public List<Map<String, String>> headers = new ArrayList<>();
    public List<List<String>> moves = new ArrayList<>();

    /**
     * Parse header section of PGN into key-value pairs
     *
     * @param headerSection Raw header text
     * @return Map of tag names to values
     */
    public Map<String, String> HeaderParser(StringBuilder headerSection) {
        Map<String, String> headers = new HashMap<>();
        String[] headerLines = headerSection.toString().trim().split("\n");
        for (String header : headerLines) {
            // Extract tag name and value by removing brackets and quotes
            String cleanString = header.replaceAll("[\\[\\]\"]", "");
            String tagName = cleanString.substring(0, header.indexOf(" ")).trim();
            String tagValue = cleanString.substring(header.indexOf(" ")).trim();
            headers.put(tagName, tagValue);
        }
        return headers;
    }


    /**
     * Extract chess moves from PGN move text
     *
     * @param moveSection Raw moves text
     * @return List of moves in algebraic notation
     */
    public List<String> MovesParser(StringBuilder moveSection) {
        String moves = moveSection.toString().trim();
        // Remove game result indicators
        moves = moves.replaceAll("1-0|0-1|1/2-1/2|\\*", "");
        // Remove comments in curly braces
        moves = moves.replaceAll("\\{[^}]*}", "");
        // Remove move numbers and continuations
        moves = moves.replaceAll("\\d+\\.\\.\\.", "").replaceAll("\\d+\\.", "");
        // Split into individual moves
        String[] moveArray = moves.trim().split("\\s+");
        return Arrays.asList(moveArray);
    }


    /**
     * Process a PGN file and extract games
     *
     * @param FileName Path to PGN file
     */


    // should return List<Model.ChessGame> s
    public void PGNFileParser(String FileName) {


        try {
            File pgnFile = new File(FileName);
            BufferedReader reader = new BufferedReader(new FileReader(pgnFile));
            int blankCounterPerGame = 0;

            String line;
            StringBuilder headerSection = new StringBuilder();
            StringBuilder moveSection = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    // Toggle between header and move sections
                    blankCounterPerGame = (blankCounterPerGame + 1) % 2;
                }
                line = line.trim();
                if (blankCounterPerGame == 0) {
                    // Process completed move section if available
                    if (!moveSection.isEmpty()) {
                        //   System.out.println(MovesParser(moveSection));
                        moves.add(MovesParser(moveSection));
                        moveSection = new StringBuilder();
                    }
                    headerSection.append(line).append("\n");
                }
                if (blankCounterPerGame == 1) {
                    // Process completed header section if available
                    if (!headerSection.isEmpty()) {
                        //System.out.println(headerSection);
                        headers.add(HeaderParser(headerSection));
                        headerSection = new StringBuilder();
                    }
                    moveSection.append(line).append(" ");
                }
            }

            // Process final game if present
            if (!moveSection.isEmpty()) {
                //    System.out.println(MovesParser(moveSection));
                moves.add(MovesParser(moveSection));
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}





