package net.mamesosu.utils.log.file;

import net.mamesosu.utils.Date;
import net.mamesosu.utils.log.constants.LogLevel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public interface LogSaver {
    static void save(String message, LogLevel level) {

        if(CreatePath.ensureDirectory(Path.of("logs"))) {

            String dateFormatted = Date.now().replace(" ", "_").replace(":", "-");
            Path logFilePath = Path.of("logs",  level.name() + "_" + dateFormatted + ".log");

            if (CreateLog.ensureFile(logFilePath)) {
                try {
                    Files.writeString(logFilePath, message, StandardOpenOption.APPEND);
                } catch (Exception e) {
                    // 再帰対策
                    System.out.println("ログの保存中にエラーが発生しました: " + e.getMessage());
                }
            }
        }
    }
}
