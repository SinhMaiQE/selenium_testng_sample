package utils;

import io.qameta.allure.Step;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogHelper {
    private static final Logger logger = Logger.getLogger(LogHelper.class.getName());

    @Step("{message}")
    public static void info(String message) {
        logger.info(message);
    }

    public static void warn(String message) {
        logger.warning(message);
    }

    public static void error(String message) {
        logger.severe(message);
    }

    public static void error(String message, Throwable t) {
        logger.log(Level.SEVERE, message, t);
    }
}
