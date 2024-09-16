package taskmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import taskmanager.controller.Managers;
import taskmanager.controller.adapters.EpicSerializer;
import taskmanager.controller.adapters.SubtaskSerializer;
import taskmanager.controller.adapters.TaskDeserializer;
import taskmanager.controller.adapters.TaskSerializer;
import taskmanager.controller.handlers.HistoryHandler;
import taskmanager.controller.handlers.PrioritizedHandler;
import taskmanager.controller.handlers.TaskHandler;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Arrays;

public class ServerController {
    private static TaskManager manager;
    private static HttpServer server;
    private static final int PORT = 8080;

    private ServerController() {
    }

    public static HttpServer initialize() throws IOException {
        manager = Managers.getDefault();
        start();

        return server;
    }

    public static HttpServer initialize(Path path) throws IOException {
        manager = Managers.getFileBacked(path);
        start();

        return server;
    }

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                .registerTypeAdapter(Task.class, new TaskDeserializer())
                .registerTypeAdapter(Epic.class, new EpicSerializer());
        return builder.create();
    }

    private static void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));

        server.start();

        System.out.println("Server started " + LocalTime.now());
    }

    private static void stop() {
        server.stop(0);
    }

    public static TaskManager getManager() {
        return manager;
    }

    public static HttpServer getServer() {
        return server;
    }

    public static void main(String[] args) throws IOException {
        Arrays.stream(args).findFirst().ifPresentOrElse(
                string -> {
                    try {
                        initialize(Path.of(string));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        initialize();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
