package myprojects.automation.assignment4.utils.logging;

import org.testng.Reporter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom reporting wrapper for TestNG.
 */
public class CustomReporter {

    private CustomReporter() {
    }

    /**
     * Logs action step that will be highlighted in test execution report.
     */
    public static void logAction(String message) {
        Reporter.log(String.format("<p>[%-12s] ACTION: %s</p>", LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME), message));
    }

    /**
     * Logs simple step.
     */
    public static void log(String message) {
        Reporter.log(String.format("<p>[%-12s] %s</p>", LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME), message));
    }
}
