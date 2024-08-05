package taskmanager.service;

import taskmanager.model.Task;

import java.util.List;

public interface HistoryManager {

    List<String> getHistory();

    void add(Task task);

    void remove(int id);
}
