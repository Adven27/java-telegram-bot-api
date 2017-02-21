package com.pengrad.telegrambot.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.*;

public class BotLogger {
    private BotLogger(){}

    private static final Logger LOGGER = Logger.getLogger("Telegram Bots Api");

    public static void setLevel(Level level) {
        LOGGER.setLevel(level);
    }

    public static void registerLogger(Handler handler) {
        LOGGER.addHandler(handler);
    }

    public static void severe(String tag, String msg) {
        LOGGER.severe(format(tag, msg));
    }

    private static String format(String tag, String msg) {
        return String.format("%s - %s", tag, msg);
    }

    public static void warn(String tag, String msg) {
        warning(tag, msg);
    }

    public static void debug(String tag, String msg) {
        fine(tag, msg);
    }

    public static void error(String tag, String msg) {
        severe(tag, msg);
    }

    public static void trace(String tag, String msg) {
        finer(tag, msg);
    }

    public static void warning(String tag, String msg) {
        LOGGER.warning(format(tag, msg));
    }

    public static void info(String tag, String msg) {
        LOGGER.info(format(tag, msg));
    }

    public static void config(String tag, String msg) {
        LOGGER.config(format(tag, msg));
    }

    public static void fine(String tag, String msg) {
        LOGGER.fine(format(tag, msg));
    }

    public static void finer(String tag, String msg) {
        LOGGER.finer(format(tag, msg));
    }

    public static void finest(String tag, String msg) {
        LOGGER.finest(format(tag, msg));
    }

    public static void log(Level level, String tag, Throwable throwable) {
        LOGGER.log(level, tag, throwable);
    }

    public static void log(Level level, String tag, String msg, Throwable thrown) {
        LOGGER.log(level, format(tag, msg), thrown);
    }

    public static void severe(String tag, Throwable throwable) {
        LOGGER.log(SEVERE, tag, throwable);
    }

    public static void warning(String tag, Throwable throwable) {
        LOGGER.log(WARNING, tag, throwable);
    }

    public static void info(String tag, Throwable throwable) {
        LOGGER.log(INFO, tag, throwable);
    }

    public static void config(String tag, Throwable throwable) {
        LOGGER.log(Level.CONFIG, tag, throwable);
    }

    public static void fine(String tag, Throwable throwable) {
        LOGGER.log(FINE, tag, throwable);
    }

    public static void finer(String tag, Throwable throwable) {
        LOGGER.log(FINER, tag, throwable);
    }

    public static void finest(String tag, Throwable throwable) {
        LOGGER.log(FINEST, tag, throwable);
    }

    public static void warn(String tag, Throwable throwable) {
        warning(tag, throwable);
    }

    public static void debug(String tag, Throwable throwable) {
        fine(tag, throwable);
    }

    public static void error(String tag, Throwable throwable) {
        severe(tag, throwable);
    }

    public static void trace(String tag, Throwable throwable) {
        finer(tag, throwable);
    }

    public static void severe(String msg, String tag, Throwable throwable) {
        log(Level.SEVERE, tag, msg, throwable);
    }

    public static void warning(String msg, String tag, Throwable throwable) {
        log(WARNING, tag, msg, throwable);
    }

    public static void info(String msg, String tag, Throwable throwable) {
        log(INFO, tag, msg, throwable);
    }

    public static void config(String msg, String tag, Throwable throwable) {
        log(CONFIG, tag, msg, throwable);
    }

    public static void fine(String msg, String tag, Throwable throwable) {
        log(FINE, tag, msg, throwable);
    }

    public static void finer(String msg, String tag, Throwable throwable) {
        log(FINER, tag, msg, throwable);
    }

    public static void finest(String msg, String tag, Throwable throwable) {
        log(FINEST, tag, msg, throwable);
    }

    public static void warn(String msg, String tag, Throwable throwable) {
        log(WARNING, tag, msg, throwable);
    }

    public static void debug(String msg, String tag, Throwable throwable) {
        log(FINE, tag, msg, throwable);
    }

    public static void error(String msg, String tag, Throwable throwable) {
        log(SEVERE, tag, msg, throwable);
    }

    public static void trace(String msg, String tag, Throwable throwable) {
        log(FINER, tag, msg, throwable);
    }
}