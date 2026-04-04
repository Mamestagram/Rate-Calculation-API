package net.mamesosu.api.calculate;

import net.mamesosu.Main;
import net.mamesosu.api.calculate.object.Rate;
import net.mamesosu.api.calculate.object.RateBySpeed;
import net.mamesosu.utils.log.AppLogger;
import net.mamesosu.utils.log.constants.LogLevel;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

public class CalculateRate {
    private static native double[] processData(String path);
    private static final double[] RATES = {0.7, 1.0, 1.5, 2.0};

    static {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String libName = os.contains("win") ? "rust_lib.dll" : "librust_lib.so";

            InputStream in = Main.class.getResourceAsStream("/" + libName);

            if (in == null) {
                throw new RuntimeException("ライブラリが見つかりません: " + libName);
            }

            // 一時ファイルに書き出す
            String prefix = os.contains("win") ? "rust_lib" : "librust_lib";
            String suffix = os.contains("win") ? ".dll" : ".so";

            Path tempLibFile = Files.createTempFile(prefix, suffix);

            Files.copy(in, tempLibFile, StandardCopyOption.REPLACE_EXISTING);

            System.load(tempLibFile.toAbsolutePath().toString());

        } catch (Exception e) {
            AppLogger.log(e.getMessage(), LogLevel.ERROR);
        }
    }

    public static CompletableFuture<RateBySpeed> calculateAsync(int id) {
        return CompletableFuture.supplyAsync(() -> calculate(id));
    }

    private static RateBySpeed calculate(int id) {
        if (id < 0) {
            AppLogger.log("無効なID: " + id, LogLevel.ERROR);
            return null;
        }

        Path dataPath = Path.of("../bancho.py/.data/osu", id + ".osu");

        if (!Files.exists(dataPath)) {
            AppLogger.log("データファイルが見つかりません: " + dataPath, LogLevel.ERROR);
            return null;
        }

        String absolutePath = dataPath.toAbsolutePath().toString();

        // フラットな double[] (32要素) を受け取る
        double[] flatScores = processData(absolutePath);

        if (flatScores == null || flatScores.length != 32) {
            AppLogger.log("データ処理に失敗しました: " + absolutePath, LogLevel.ERROR);
            return null;
        }

        RateBySpeed rates = new RateBySpeed();

        for (int i = 0; i < RATES.length; i++) {
            Rate r = new Rate();
            int offset = i * 8;
            r.overAll = flatScores[offset];
            r.stream = flatScores[offset + 1];
            r.jumpStream = flatScores[offset + 2];
            r.handStream = flatScores[offset + 3];
            r.stamina = flatScores[offset + 4];
            r.jackSpeed = flatScores[offset + 5];
            r.chordJack = flatScores[offset + 6];
            r.technical = flatScores[offset + 7];

            rates.rates.put(RATES[i], r);
        }

        return rates;
    }
}
