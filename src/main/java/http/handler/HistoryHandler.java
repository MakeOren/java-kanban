package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpStatusCode;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if (method.equals("GET") && path.equals("/history")) {
                handleGetHistory(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (IOException e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> tasks = manager.getHistory();
        String json = gson.toJson(tasks);
        sendText(exchange, json, HttpStatusCode.OK.getCode());
    }
}
