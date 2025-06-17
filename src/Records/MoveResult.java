package Records;

/**
 * Result of attempting to execute a chess move
 */
public record MoveResult(boolean success, String error, Position from, Position to) {

    /**
     * Create a successful move result
     */
    public static MoveResult success(Position from, Position to) {
        return new MoveResult(true, "", from, to);
    }


    /**
     * Create a failed move result
     */
    public static MoveResult failure(String error) {
        return new MoveResult(false, error, null, null);
    }

    /**
     * Create a failed move result with position context
     */
    public static MoveResult failure(String error, Position attempted) {
        return new MoveResult(false, error, null, attempted);
    }

    /**
     * Check if move was successful
     */
    public boolean isValid() {
        return success;
    }

    /**
     * Get error message or empty string if successful
     */
    public String getErrorMessage() {
        return error != null ? error : "";
    }
}