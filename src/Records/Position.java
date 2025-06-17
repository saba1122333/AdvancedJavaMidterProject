package Records;

public record Position(int row, int col) {

    public Position {
        if (row < 0 || row > 7) {
            throw new IllegalArgumentException("Row must be 0-7, got: " + row);
        }
        if (col < 0 || col > 7) {
            throw new IllegalArgumentException("Column must be 0-7, got: " + col);
        }
    }

    public static Position fromNotation(String notation) {
        if (notation.length() != 2) {
            throw new IllegalArgumentException("Invalid notation: " + notation);
        }

        char file = notation.charAt(0);
        char rank = notation.charAt(1);

        int col = file - 'a';
        int row = 8 - Character.getNumericValue(rank);

        return new Position(row, col);
    }


    public String toNotation() {
        char file = (char) ('a' + col);
        char rank = (char) ('1' + (7 - row));
        return "" + file + rank;
    }


    public boolean isValid() {
        return row >= 0 && row <= 7 && col >= 0 && col <= 7;
    }
}
