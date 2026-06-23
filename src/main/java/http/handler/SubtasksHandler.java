package http.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import manager.TaskManager;
import task.SubTask;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (method.equals("GET") && path.matches("/subtasks/\\d+")) {
                handleGetSubtask(exchange, path);
            } else if (method.equals("GET") && path.equals("/subtasks")) {
                handleGetSubtasks(exchange);
            } else if (method.equals("DELETE") && path.matches("/subtasks/\\d+")) {
                handleDeleteSubtasksById(exchange, path);
            } else if (method.equals("POST") && path.equals("/subtasks")) {
                handleCreateOrUpdateSubtasks(exchange);
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

    private void handleDeleteSubtasksById(HttpExchange exchange, String path) throws IOException {
        int id = extractId(path);
        manager.getSubTaskOrThrow(id);
        manager.deleteSubTaskById(id);
        sendText(exchange, String.format("Задача id = %d удалена", id), 200);

    }

    private void handleCreateOrUpdateSubtasks(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        SubTask subTask = gson.fromJson(body, SubTask.class);
        if (subTask.getId() == 0) {
            manager.getEpicOrThrow(subTask.getEpicId());
            SubTask createSubtask = manager.addSubTask(subTask);
            if (createSubtask == null) {
                sendHasInteractions(exchange);
                return;
            }
            String json = gson.toJson(createSubtask);
            sendText(exchange, json, 201);
        } else {
            manager.getSubTaskOrThrow(subTask.getId());
            SubTask updateSubtask = manager.updateSubTask(subTask);
            String json = gson.toJson(updateSubtask);
            if (updateSubtask == null) {
                sendHasInteractions(exchange);
                return;
            } else {
                sendText(exchange, json, 201);
            }
        }

    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        List<SubTask> subTasks = manager.getSubTasksList();
        String json = gson.toJson(subTasks);
        sendText(exchange, json, 200);
    }

    private void handleGetSubtask(HttpExchange exchange, String path) throws IOException {
        int id = extractId(path);
        SubTask subTask = manager.getSubTaskOrThrow(id);
        String json = gson.toJson(subTask);
        sendText(exchange, json, 200);
    }
}
