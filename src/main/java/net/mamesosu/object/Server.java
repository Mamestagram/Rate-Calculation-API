package net.mamesosu.object;

import com.sun.net.httpserver.HttpServer;
import io.github.cdimascio.dotenv.Dotenv;
import net.mamesosu.api.CalculateHandler;
import net.mamesosu.utils.log.AppLogger;
import net.mamesosu.utils.log.WebLogger;
import net.mamesosu.utils.log.constants.LogLevel;
import net.mamesosu.utils.web.Response;

import java.net.InetSocketAddress;

public class Server implements WebLogger, Response {

    int port;

    public Server () {
        Dotenv dotenv = Dotenv.configure().load();
        port = Integer.parseInt(dotenv.get("PORT"));
    }

    public void start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // 404返すように
            server.createContext("/", exchange -> {
                print(exchange);
                send(exchange, "{}", 404);
                exchange.getRequestBody().close();
            });

            server.createContext("/calculate", new CalculateHandler());

            AppLogger.log("Server started on port " + port, LogLevel.INFO);

        } catch (Exception e) {
            AppLogger.log(e.getMessage(), LogLevel.ERROR);
        }
    }
}
