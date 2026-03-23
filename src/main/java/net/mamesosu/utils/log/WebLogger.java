package net.mamesosu.utils.log;

import com.sun.net.httpserver.HttpExchange;
import net.mamesosu.utils.log.constants.LogLevel;

public interface WebLogger {

    default void print(HttpExchange exchange) {
        String ip = exchange.getRemoteAddress().getAddress().getHostAddress();
        AppLogger.log(exchange.getRequestMethod() + ": " + exchange.getRequestURI().getPath() + "?" + exchange.getRequestURI().getQuery() + " (" + ip + ")", LogLevel.INFO);
    }
}
