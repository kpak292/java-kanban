package taskmanager.controller;

import com.sun.net.httpserver.HttpServer;
import taskmanager.controller.handlers.TasksHandler;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ServerController {
    private static TaskManager manager;
    private static HttpServer server;
    private static final int PORT = 8080;

    private ServerController() {
    }


    public static void initialize() throws IOException {
        manager = Managers.getDefault();
        start();
    }

    public static void initialize(Path path) throws IOException {
        manager = Managers.getFileBacked(path);
        start();
    }

    private static void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler(manager));
        server.start();
        System.out.println("Server started " + LocalTime.now());
        addtestdata(manager);
    }

    public static void addtestdata(TaskManager manager) {
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");

        manager.addTask(epic1);//1
        manager.addTask(epic2);//2

        Task task1 = new Task("Task1", "Description3");
        Task task2 = new Task("Task2", "Description4");

        LocalDateTime start1 = LocalDateTime.of(2024, 1, 1, 9, 00);
        Duration duration = Duration.ofMinutes(90);

        task1.setStartTime(start1);
        task1.setDuration(duration);

        task2.setStartTime(start1.plusHours(2));
        task2.setDuration(duration);

        manager.addTask(task1);//3
        manager.addTask(task2);//4

        Subtask subTask1 = new Subtask("Subtask1", "Description5", epic1.getId());
        Subtask subTask2 = new Subtask("Subtask2", "Description6", epic2.getId());
        Subtask subTask3 = new Subtask("Subtask3", "Description7", epic2.getId());

        subTask1.setStartTime(start1.plusHours(4));
        subTask1.setDuration(duration);

        subTask2.setStartTime(start1.plusHours(6));
        subTask2.setDuration(duration);

        subTask3.setStartTime(start1.plusHours(8));
        subTask3.setDuration(duration);

        manager.addTask(subTask1);//5
        manager.addTask(subTask2);//6
        manager.addTask(subTask3);//7
    }


}
