package net.mamesosu.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.mamesosu.api.calculate.CalculateRate;
import net.mamesosu.utils.log.WebLogger;
import net.mamesosu.utils.web.Param;
import net.mamesosu.utils.web.Response;

import java.util.Map;

public class CalculateHandler implements HttpHandler, WebLogger, Param, Response {

    @Override
    public void handle(HttpExchange exchange) {
        print(exchange);

        Map<String, String> queryParams = getQueryParameters(exchange.getRequestURI().getQuery());

        // ID以外のクエリパラメータがあったらエラー
        if(queryParams.size() != 1) {
            send(exchange, "{\"error\": \"Invalid query parameters\"}", 400);
            return;
        }

        // IDクエリパラメータがなかったらエラー
        if(!queryParams.containsKey("id")) {
            send(exchange, "{\"error\": \"Missing 'id' query parameter\"}", 400);
            return;
        }

        int id = Integer.parseInt(queryParams.get("id"));

        CalculateRate.calculateAsync(id).thenAccept(
                sendJson(exchange)
        );
    }
}
