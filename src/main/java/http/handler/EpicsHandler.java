package http.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import manager.TaskManager;
import task.Epic;
import task.SubTask;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (method.equals("GET") && path.matches("/epics/\\d+/subtasks")) {
                handleGetEpicSubtasks(exchange, path);
            } else if (method.equals("GET") && path.matches("/epics/\\d+")) {
                handleGetEpic(exchange, path);
            } else if (method.equals("GET") && path.matches("/epics")) {
                handleGetEpics(exchange);
            } else if (method.equals("DELETE") && path.matches("/epics/\\d+")) {
                handleDeleteEpic(exchange, path);
            } else if (method.equals("POST") && path.matches("/epics")) {
                handleCreateEpic(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = manager.getEpicList();
        String json = gson.toJson(epics);
        sendText(exchange, json, 200);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, String path) throws IOException {
        int epicId = extractId(path);
        manager.getEpicOrThrow(epicId);
        List<SubTask> subTasks = manager.getSubTasksByEpic(epicId);
        String json = gson.toJson(subTasks);
        sendText(exchange, json, 200);
    }

    private void handleGetEpic(HttpExchange exchange, String path) throws IOException {
        int epicId = extractId(path);
        Epic epic = manager.getEpicOrThrow(epicId);
        String json = gson.toJson(epic);
        sendText(exchange, json, 200);
    }

    private void handleDeleteEpic(HttpExchange exchange, String path) throws IOException {
        int epicId = extractId(path);
        manager.getEpicOrThrow(epicId);
        manager.deleteEpicById(epicId);
        sendText(exchange, String.format("Задача id = %d удалена", epicId), 200);
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Epic epic = gson.fromJson(body, Epic.class);

        Epic createEpic = manager.addEpic(epic);
        if (createEpic == null) {
            sendHasInteractions(exchange);
            return;
        }
        String json = gson.toJson(createEpic);
        sendText(exchange, json, 201);

    }

}
