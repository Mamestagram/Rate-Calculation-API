package net.mamesosu.utils.log.file;

import net.mamesosu.utils.log.AppLogger;
import net.mamesosu.utils.log.constants.LogLevel;

import java.nio.file.Files;
import java.nio.file.Path;

public interface CreatePath {

    static boolean ensureDirectory (Path dir) {

        try {
            if (Files.notExists(dir)) {
                Files.createDirectories(dir);
                AppLogger.log("新規のログフォルダ: " + dir + "を作成しました.", LogLevel.INFO);
            }
        } catch (Exception e) {
            AppLogger.log("フォルダ作成中にエラーが発生しました: " + e.getMessage(), LogLevel.ERROR);
            return false;
        }

        return true;
    }
}
