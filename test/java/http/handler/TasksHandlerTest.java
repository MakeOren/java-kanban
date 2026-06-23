package http.handler;

import com.google.gson.Gson;
import http.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TasksHandlerTest {

    private TaskManager manager;
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        manager.clearTasks();
        manager.clearSubTasks();
        manager.clearEpics();
        gson = HttpTaskServer.getGson();
        server = new HttpTaskServer(manager, gson);
        server.serverStart();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.serverStop();
    }

    @Test
    void shouldGetEmptyTasksList() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void shouldCreateTask() throws Exception {
        Task task = new Task("Task1", "Desc", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        task.setDuration(Duration.ofMinutes(30));


        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getTasksList().size());
    }

    @Test
    void shouldGetTaskById() throws Exception {
        Task task = new Task("Task1", "Desc", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        task.setDuration(Duration.ofMinutes(30));
        manager.addTask(task);
        int id = task.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task1"));
    }

    @Test
    void shouldUpdateTask() throws Exception {
        Task task = new Task("Old name", "Old desc", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        task.setDuration(Duration.ofMinutes(30));
        manager.addTask(task);
        int id = task.getId();

        Task updated = new Task("New name", "New desc", TaskStatus.IN_PROGRESS);
        updated.setStartTime(LocalDateTime.of(2024, 1, 1, 13, 0));
        updated.setDuration(Duration.ofMinutes(45));
        updated.setId(id);
        String json = gson.toJson(updated);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("New name", manager.getTaskById(id).getTitle());
    }

    @Test
    void shouldDeleteTask() throws Exception {
        Task task = new Task("To delete", "Desc", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        task.setDuration(Duration.ofMinutes(30));
        manager.addTask(task);
        int id = task.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getTaskById(id));
    }

    @Test
    void shouldReturn404WhenTaskNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistent() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldReturn406WhenOverlap() throws Exception {
        Task task1 = new Task("Task 1", "Desc", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        task1.setDuration(Duration.ofMinutes(60));
        manager.addTask(task1);

        Task task2 = new Task("Task 2", "Desc", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 30)); // пересекается!
        task2.setDuration(Duration.ofMinutes(60));
        String json = gson.toJson(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    void shouldReturn400WhenInvalidJson() throws Exception {
        String invalidJson = "{\"name\":\"Test\",}"; // ← лишняя запятая

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }
}
