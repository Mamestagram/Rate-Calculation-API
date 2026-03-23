package net.mamesosu.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.mamesosu.api.calculate.CalculateRate;
import net.mamesosu.api.calculate.object.RateBySpeed;
import net.mamesosu.utils.log.AppLogger;
import net.mamesosu.utils.log.WebLogger;
import net.mamesosu.utils.log.constants.LogLevel;
import net.mamesosu.utils.web.Param;
import net.mamesosu.utils.web.Response;

import java.util.Map;

public class CalculateHandler implements HttpHandler, WebLogger, Param, Response {

    @Override
    public void handle(HttpExchange exchange) {
        print(exchange);

        Map<String, String> queryParams = getQueryParameters(exchange.getRequestURI().getQuery());
        ObjectMapper mapper = new ObjectMapper();

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
        RateBySpeed rates = CalculateRate.calculate(id);

        if(rates == null) {
            send(exchange, "{\"error\": \"Invalid ID\"}", 400);
            return;
        }

        try {
            String json = mapper.writeValueAsString(rates);
            send(exchange, json, 200);
        } catch (Exception e) {
            send(exchange, "{\"error\": \"Internal Server Error\"}", 500);
            AppLogger.log(e.getMessage(), LogLevel.ERROR);
        }
    }
}
