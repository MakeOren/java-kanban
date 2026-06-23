package http.handler;

import com.google.gson.Gson;
import http.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksHandlerTest {

    private TaskManager manager;
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson;

    @BeforeEach
    void setUp() throws IOException {
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
    void shouldGetEmptySubtasksList() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void shouldCreateSubtask() throws Exception {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        SubTask subTask = new SubTask("Subtask1", "Desc", TaskStatus.NEW);
        subTask.setEpicId(epic.getId());
        subTask.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        subTask.setDuration(Duration.ofMinutes(30));
        String json = gson.toJson(subTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getSubTasksList().size());
    }

    @Test
    void shouldGetSubtaskById() throws Exception {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        SubTask subTask = new SubTask("Subtask1", "Desc", TaskStatus.NEW);
        subTask.setEpicId(epic.getId());
        subTask.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        subTask.setDuration(Duration.ofMinutes(30));
        manager.addSubTask(subTask);
        int id = subTask.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask1"));
    }

    @Test
    void shouldUpdateSubtask() throws Exception {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        SubTask subTask = new SubTask("Old name", "Old desc", TaskStatus.NEW);
        subTask.setEpicId(epic.getId());
        subTask.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        subTask.setDuration(Duration.ofMinutes(30));
        manager.addSubTask(subTask);
        int id = subTask.getId();

        SubTask updated = new SubTask("New name", "New desc", TaskStatus.IN_PROGRESS);
        updated.setEpicId(epic.getId());
        updated.setStartTime(LocalDateTime.of(2024, 1, 1, 13, 0));
        updated.setDuration(Duration.ofMinutes(45));
        updated.setId(id);
        String json = gson.toJson(updated);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("New name", manager.getSubTaskById(id).getTitle());
    }

    @Test
    void shouldDeleteSubtask() throws Exception {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        SubTask subTask = new SubTask("To delete", "Desc", TaskStatus.NEW);
        subTask.setEpicId(epic.getId());
        subTask.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        subTask.setDuration(Duration.ofMinutes(30));
        manager.addSubTask(subTask);
        int id = subTask.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getSubTaskById(id));
    }

    @Test
    void shouldReturn404WhenSubtaskNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentSubtask() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/999"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldReturn406WhenSubtaskOverlap() throws Exception {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        SubTask subTask1 = new SubTask("Subtask 1", "Desc", TaskStatus.NEW);
        subTask1.setEpicId(epic.getId());
        subTask1.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        subTask1.setDuration(Duration.ofMinutes(60));
        manager.addSubTask(subTask1);

        SubTask subTask2 = new SubTask("Subtask 2", "Desc", TaskStatus.NEW);
        subTask2.setEpicId(epic.getId());
        subTask2.setStartTime(LocalDateTime.of(2024, 1, 1, 12, 30));
        subTask2.setDuration(Duration.ofMinutes(60));
        String json = gson.toJson(subTask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    void shouldReturn400WhenInvalidJsonForSubtask() throws Exception {
        String invalidJson = "{\"name\":\"Test\",}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }
}