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
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class CalculateRate {
    public static native HashMap<Double, double[]> processData(String path);

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

    public static RateBySpeed calculate(int id) {
        Path dataPath = Path.of("../bancho.py/.data/osu", id + ".osu");

        if (id < 0) {
            AppLogger.log("無効なID: " + id, LogLevel.ERROR);
            return null;
        }

        if (!Files.exists(dataPath)) {
            AppLogger.log("データファイルが見つかりません: " + dataPath, LogLevel.ERROR);
            return null;
        }

        String absolutePath = dataPath.toAbsolutePath().toString();

        System.out.println("Processing file: " + absolutePath);

        HashMap<Double, double[]> result = processData(absolutePath);

        System.out.println("Processing completed for file: " + absolutePath);

        if (result == null) {
            AppLogger.log("データ処理に失敗しました: " + absolutePath, LogLevel.ERROR);
            return null;
        }

        RateBySpeed rates = new RateBySpeed();

        result.forEach((rate, scores) -> {
            Rate r = new Rate();
            r.overAll = scores[0];
            r.stream = scores[1];
            r.jumpStream = scores[2];
            r.handStream = scores[3];
            r.stamina = scores[4];
            r.jackSpeed = scores[5];
            r.chordJack = scores[6];
            r.technical = scores[7];

            rates.rates.put(rate, r);
        });

        return rates;
    }
}
