package taskmanager;

import com.sun.net.httpserver.HttpServer;
import taskmanager.controller.Managers;
import taskmanager.controller.handlers.TasksHandler;
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

    private static void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        TasksHandler handler = new TasksHandler(manager);
        server.createContext("/tasks", handler);
        server.createContext("/history", handler);
        server.createContext("/prioritized", handler);

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
