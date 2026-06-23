package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.sun.net.httpserver.HttpServer;
import http.handler.*;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private TaskManager manager;
    private Gson gson;
    private HttpServer httpServer;

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Gson gson = new Gson();
        HttpTaskServer server = new HttpTaskServer(manager, gson);
        server.serverStart();
    }

    public HttpTaskServer(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    public void serverStart() {

        try {
            httpServer = HttpServer.create();

            httpServer.bind(new InetSocketAddress(PORT), 0);

            httpServer.createContext("/tasks", new TasksHandler(manager, gson));
            httpServer.createContext("/epics", new EpicsHandler(manager, gson));
            httpServer.createContext("/subtasks", new SubtasksHandler(manager, gson));
            httpServer.createContext("/history", new HistoryHandler(manager, gson));
            httpServer.createContext("/prioritized", new PrioritizedHandler(manager, gson));

            httpServer.start();
            System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void serverStop() {
        if (httpServer != null) {
            httpServer.stop(0);
            System.out.println("HTTP-сервер остановлен");
        }
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (src, type, context) ->
                                context.serialize(src.toString()))
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, context) ->
                                LocalDateTime.parse(json.getAsString()))
                .registerTypeAdapter(Duration.class,
                        (JsonSerializer<Duration>) (src, type, context) ->
                                context.serialize(src.toSeconds()))
                .registerTypeAdapter(Duration.class,
                        (JsonDeserializer<Duration>) (json, type, context) ->
                                Duration.ofSeconds(json.getAsLong()))
                .create();
    }
}
