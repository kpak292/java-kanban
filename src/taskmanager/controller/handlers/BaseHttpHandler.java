package taskmanager.controller.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static void writeResponse(HttpExchange exchange,
                                     String responseString,
                                     int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            /* Alowed Codes
            200 - application/json
            201 - text/plain
            500 - taxt/plain
            404 - text/plain
             */

            if (responseCode == 200) {
                exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
            } else {
                exchange.getResponseHeaders().set("Content-Type", "text/plain;charset=utf-8");
            }

            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

}
