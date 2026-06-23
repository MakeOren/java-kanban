package http.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (method.equals("GET") && path.matches("/tasks/\\d+")) {
                handleGetTask(exchange, path);
            } else if (method.equals("GET") && path.equals("/tasks")) {
                handleGetTasks(exchange);
            } else if (method.equals("POST") && path.equals("/tasks")) {
                handleCreateOrUpdateTask(exchange);
            } else if (method.equals("DELETE") && path.matches("/tasks/\\d+")) {
                handleDeleteTaskById(exchange, path);
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

    private void handleDeleteTaskById(HttpExchange exchange, String path) throws IOException {
        int id = extractId(path);
        manager.getTaskOrThrow(id);
        manager.deleteTaskById(id);
        sendText(exchange, String.format("Задача id = %d удалена", id), 200);

    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Task task = gson.fromJson(body, Task.class);
        if (task.getId() == 0) {
            Task createTask = manager.addTask(task);
            if (createTask == null) {
                sendHasInteractions(exchange);
                return;
            }
            String json = gson.toJson(createTask);
            sendText(exchange, json, 201);
        } else {
            manager.getTaskOrThrow(task.getId());
            Task updateTask = manager.updateTask(task);
            String json = gson.toJson(updateTask);
            if (updateTask == null) {
                sendHasInteractions(exchange);
                return;
            } else {
                sendText(exchange, json, 201);
            }
        }

    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = manager.getTasksList();
        String json = gson.toJson(tasks);
        sendText(exchange, json, 200);
    }

    private void handleGetTask(HttpExchange exchange, String path) throws IOException {
        int id = extractId(path);
        Task task = manager.getTaskOrThrow(id);
        String json = gson.toJson(task);
        sendText(exchange, json, 200);
    }


}
