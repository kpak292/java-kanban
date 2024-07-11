package taskmanager.controller;

import taskmanager.service.HistoryManager;
import taskmanager.service.TaskManager;
import taskmanager.service.implementation.InMemoryHistoryManager;
import taskmanager.service.implementation.InMemoryTaskManager;

public class Managers {
    private static InMemoryTaskManager inMemoryTaskManager;
    private static InMemoryHistoryManager inMemoryHistoryManager;


    public static TaskManager getDefault() {
        if (inMemoryTaskManager == null) {
            inMemoryTaskManager = new InMemoryTaskManager(getDefaultHistory());
        }

        return inMemoryTaskManager;
    }

    public static HistoryManager getDefaultHistory() {
        if (inMemoryHistoryManager == null) {
            inMemoryHistoryManager = new InMemoryHistoryManager();
        }

        return inMemoryHistoryManager;
    }
}
