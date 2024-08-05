package taskmanager.service.implementation;

import taskmanager.model.Task;
import taskmanager.service.HistoryManager;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HistoryLog history = new HistoryLog();

    //Получение истории просмотра тасков
    @Override
    public List<String> getHistory() {
        return history.getLog();
    }

    //Метод добавления данных в историю
    @Override
    public void add(Task task) {
        history.add(task);
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }
}

