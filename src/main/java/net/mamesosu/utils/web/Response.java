package net.mamesosu.utils.web;

import com.sun.net.httpserver.HttpExchange;
import net.mamesosu.utils.log.AppLogger;
import net.mamesosu.utils.log.constants.LogLevel;

import java.nio.charset.StandardCharsets;

public interface Response {

    default void send(HttpExchange exchange, String json, int statusCode) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, json.getBytes(StandardCharsets.UTF_8).length);
            exchange.getResponseBody().write(json.getBytes(StandardCharsets.UTF_8));
            exchange.getResponseBody().close();
        } catch (Exception e) {
            AppLogger.log(e.getMessage(), LogLevel.ERROR);
        }
    }
}
