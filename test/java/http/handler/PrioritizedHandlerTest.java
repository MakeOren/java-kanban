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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrioritizedHandlerTest {

    private TaskManager manager;
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        manager = new InMemoryTaskManager();
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
    void shouldGetEmptyPrioritizedList() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void shouldGetPrioritizedTasks() throws Exception {
        Task task1 = new Task("Task1", "Desc", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        task1.setDuration(Duration.ofMinutes(30));
        manager.addTask(task1);

        Task task2 = new Task("Task2", "Desc", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        task2.setDuration(Duration.ofMinutes(30));
        manager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task2"));
        assertTrue(response.body().contains("Task1"));
    }

    @Test
    void shouldReturn404ForNonExistentPrioritizedEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}