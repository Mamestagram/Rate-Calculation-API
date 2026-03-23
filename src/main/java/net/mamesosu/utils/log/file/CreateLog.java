package net.mamesosu.utils.log.file;

import net.mamesosu.utils.log.AppLogger;
import net.mamesosu.utils.log.constants.LogLevel;

import java.nio.file.Files;
import java.nio.file.Path;

public interface CreateLog {

    static boolean ensureFile (Path file) {

        try {
            Path parent = file.getParent();

            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }

            if (Files.notExists(file)) {
                Files.createFile(file);
                AppLogger.log("新規のログファイル: " + file + "を作成しました.", LogLevel.INFO);
            }
        } catch (Exception e) {
            AppLogger.log("ファイル作成中にエラーが発生しました: " + e.getMessage(), LogLevel.ERROR);
            return false;
        }

        return true;
    }
}
