package com.ciluu.smcl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.*;

public class SmclLogger {
    public static final Logger LOGGER = Logger.getLogger(SmclLogger.class.getName());

    static {
        try {
            Logger rootLogger = Logger.getLogger("");
            for (Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }

            ConsoleHandler handler = new ConsoleHandler() {{
                setOutputStream(System.out);
            }};
            handler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    return String.format(
                            "[%1$tT] [%2$s/%3$s] %4$s%n%5$s",
                            record.getMillis(),
                            Thread.currentThread().getName(),
                            record.getLevel().getName(),
                            record.getMessage(),
                            record.getThrown() != null ?
                                    exceptionToString(record.getThrown()) : ""
                    );
                }
            });
            handler.setLevel(Level.ALL);

            LOGGER.setLevel(Level.ALL);
            LOGGER.setUseParentHandlers(false);
            LOGGER.addHandler(handler);

            Thread.setDefaultUncaughtExceptionHandler((t, e) ->
                    LOGGER.log(Level.SEVERE, "Uncaught exception in thread " + t.getName(), e)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize logger", e);
        }
    }

    private SmclLogger() {
    }

    public static String exceptionToString(Throwable thrown) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        thrown.printStackTrace(printWriter);
        return stringWriter.toString();

    }
}