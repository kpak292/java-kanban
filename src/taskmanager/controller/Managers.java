package taskmanager.controller;

import taskmanager.service.HistoryManager;
import taskmanager.service.TaskManager;
import taskmanager.service.implementation.FileBackedTaskManager;
import taskmanager.service.implementation.InMemoryHistoryManager;
import taskmanager.service.implementation.InMemoryTaskManager;

import java.nio.file.Path;

public class Managers {
    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    private Managers() {

    }

    public static TaskManager getDefault() {
        if (taskManager == null) {
            taskManager = new InMemoryTaskManager(getDefaultHistory());
        }

        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }

        return historyManager;
    }

    public static TaskManager getFileBacked(Path path) {
        return new FileBackedTaskManager(path);
    }
}
