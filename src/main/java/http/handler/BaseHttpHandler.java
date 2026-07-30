package http.handler;

import com.sun.net.httpserver.HttpExchange;
import http.HttpStatusCode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected void sendText(HttpExchange h, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        h.sendResponseHeaders(HttpStatusCode.NOT_FOUND.getCode(), -1);
        h.close();
    }

    protected void sendInternalError(HttpExchange h) throws IOException {
        h.sendResponseHeaders(HttpStatusCode.INTERNAL_ERROR.getCode(), -1);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        h.sendResponseHeaders(HttpStatusCode.NOT_ACCEPTABLE.getCode(), -1);
        h.close();
    }

    protected int extractId(String path) {
        String[] parts = path.split("/");

        if (parts[parts.length - 1].equals("subtasks")) {
            return Integer.parseInt(parts[parts.length - 2]);
        } else {
            return Integer.parseInt(parts[parts.length - 1]);
        }
    }

    protected String readBody(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void sendBadRequest(HttpExchange h) throws IOException {
        h.sendResponseHeaders(HttpStatusCode.BAD_REQUEST.getCode(), -1);
        h.close();
    }

}
