package net.mamesosu.utils.log;

import net.mamesosu.utils.Date;
import net.mamesosu.utils.log.constants.LogLevel;
import net.mamesosu.utils.log.file.LogSaver;

public interface AppLogger {

    static void log(String message, LogLevel level) {

        String log = Date.now() + " [API] [" + level + "] " + message;

        if (level.equals(LogLevel.ERROR) || level.equals(LogLevel.FATAL)) {
            LogSaver.save(log + System.lineSeparator(), level);
        }

        System.out.println(log);
    }
}
