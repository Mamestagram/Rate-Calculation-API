package net.mamesosu.utils.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import net.mamesosu.utils.log.AppLogger;
import net.mamesosu.utils.log.constants.LogLevel;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

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

    default <T>Consumer<T> sendJson(HttpExchange exchange) {
        return obj -> {
            try {
                if (obj == null) {
                    send(exchange, "{}", 500);
                    return;
                }

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(obj);

                send(exchange, json, 200);
            } catch (Exception e) {
                AppLogger.log(e.getMessage(), LogLevel.ERROR);
                send(exchange, "Internal Server Error", 500);
            }
        };
    }
}
