package taskmanager.service.implementation;

import taskmanager.model.Task;
import taskmanager.service.HistoryManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<String> history = new ArrayList<>();
    private static final int HISTORY_LIMIT = 10;

    //Получение истории просмотра тасков
    @Override
    public List<String> getHistory() {
        return new ArrayList<>(history);
    }

    //Метод добавления данных в историю
    @Override
    public void add(Task task) {
        if (history.size() == HISTORY_LIMIT) {
            history.remove(0);
        }

        String historyData = "";

        SimpleDateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy HH:mm: ");

        historyData += dateformat.format(new Date()) +
                "ID:" + task.getId() + " " +
                "Type:" + task.getClass().getSimpleName() + " " +
                "Name:" + task.getName();

        history.add(historyData);
    }
}

