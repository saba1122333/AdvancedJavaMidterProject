package Util;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Handler;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.file.Paths;

public class GameLogger {
    // Main application logger
    private static final Logger MAIN_LOGGER = Logger.getLogger("ChessApplication");
    private static boolean initialized = false;

    // Thread-specific context
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    // File-specific loggers (one per PGN file)
    private static final Map<String, Logger> FILE_LOGGERS = new ConcurrentHashMap<>();

    /**
     * Custom formatter for cleaner log output with thread and context information
     */
    private static class ChessLogFormatter extends Formatter {
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();

            // Get the thread name
            String threadName = Thread.currentThread().getName();

            // Get context if available
            String context = CONTEXT.get();
            String contextInfo = context != null ? "[" + context + "] " : "";

            // Format: [LEVEL] [Time] [ThreadName] [Context] Message
            sb.append("[")
                    .append(record.getLevel().getName())
                    .append("] [")
                    .append(dateFormat.format(new Date(record.getMillis())))
                    .append("] [")
                    .append(threadName)
                    .append("] ")
                    .append(contextInfo)
                    .append(formatMessage(record))
                    .append(System.lineSeparator());

            // Add exception info if present
            if (record.getThrown() != null) {
                Throwable thrown = record.getThrown();
                sb.append("Exception: ")
                        .append(thrown.getMessage())
                        .append(System.lineSeparator());

                // Add first few lines of stack trace
                StackTraceElement[] stackTrace = thrown.getStackTrace();
                int tracesToShow = Math.min(3, stackTrace.length);
                for (int i = 0; i < tracesToShow; i++) {
                    sb.append("    at ")
                            .append(stackTrace[i])
                            .append(System.lineSeparator());
                }
                if (stackTrace.length > tracesToShow) {
                    sb.append("    ... ").append(stackTrace.length - tracesToShow)
                            .append(" more").append(System.lineSeparator());
                }
            }

            return sb.toString();
        }
    }

    /**
     * Initialize the main logger
     */
    public static void init() {
        if (initialized) return;

        try {
            // Remove existing handlers from the logger
            for (Handler handler : MAIN_LOGGER.getHandlers()) {
                MAIN_LOGGER.removeHandler(handler);
            }

            // Make sure we don't use parent handlers
            MAIN_LOGGER.setUseParentHandlers(false);

            // Create our custom formatter
            ChessLogFormatter formatter = new ChessLogFormatter();

            // Create and configure file handler for main log
            FileHandler fileHandler = new FileHandler("Games.log", true);
            fileHandler.setFormatter(formatter);
            MAIN_LOGGER.addHandler(fileHandler);

            // Create and configure console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(formatter);
            MAIN_LOGGER.addHandler(consoleHandler);

            // Configure logger
            MAIN_LOGGER.setLevel(Level.ALL);

            initialized = true;
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    /**
     * Set the current thread's context (e.g., PGN file being processed)
     */
    public static void setContext(String context) {
        CONTEXT.set(context);
    }

    /**
     * Clear the current thread's context
     */
    public static void clearContext() {
        CONTEXT.remove();
    }

    /**
     * Get or create a file-specific logger
     */
    public static Logger getFileLogger(String pgnFilePath) {
        return FILE_LOGGERS.computeIfAbsent(pgnFilePath, path -> {
            Logger logger = Logger.getLogger("ChessApplication." + path);
            try {
                // Remove existing handlers from the logger
                for (Handler handler : logger.getHandlers()) {
                    logger.removeHandler(handler);
                }

                // Make sure we don't use parent handlers
                logger.setUseParentHandlers(false);

                // Extract filename for the log file name
                String fileName = Paths.get(path).getFileName().toString();
                String logFileName = "chess_" + fileName.replaceAll("[^a-zA-Z0-9.-]", "_") + ".log";

                // Set up the file handler with our formatter
                FileHandler fileHandler = new FileHandler(logFileName, true);
                fileHandler.setFormatter(new ChessLogFormatter());
                logger.addHandler(fileHandler);

                // Configure the logger
                logger.setLevel(Level.ALL);
            } catch (IOException e) {
                MAIN_LOGGER.severe("Failed to create logger for file: " + path + " - " + e.getMessage());
            }
            return logger;
        });
    }

    // Standard logging methods that use the main logger
    public static void info(String message) {
        getLogger().info(message);
    }

    public static void warning(String message) {
        getLogger().warning(message);
    }

    public static void error(String message) {
        getLogger().severe(message);
    }

    public static void error(String message, Throwable e) {
        getLogger().log(Level.SEVERE, message, e);
    }

    // File-specific logging methods
    public static void fileInfo(String pgnFilePath, String message) {
        getFileLogger(pgnFilePath).info(message);
    }

    public static void fileWarning(String pgnFilePath, String message) {
        getFileLogger(pgnFilePath).warning(message);
    }

    public static void fileError(String pgnFilePath, String message) {
        getFileLogger(pgnFilePath).severe(message);
    }

    public static void fileError(String pgnFilePath, String message, Throwable e) {
        getFileLogger(pgnFilePath).log(Level.SEVERE, message, e);
    }

    private static Logger getLogger() {
        if (!initialized) init();
        return MAIN_LOGGER;
    }
}