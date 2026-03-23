package net.mamesosu.utils.web;

import net.mamesosu.utils.log.AppLogger;
import net.mamesosu.utils.log.constants.LogLevel;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public interface Param {

    default Map<String, String> getQueryParameters(String query) {
        try {
            Map<String, String> params = new HashMap<>();

            if (query == null || query.isEmpty()) return params;
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2) {
                    params.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8), URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
                }
            }
            return params;
        } catch (Exception e) {
            AppLogger.log(e.getMessage(), LogLevel.ERROR);
            return new HashMap<>();
        }
    }
}
