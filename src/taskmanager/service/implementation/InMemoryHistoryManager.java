package taskmanager.service.implementation;

import taskmanager.model.Task;
import taskmanager.service.HistoryManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<String> history = new ArrayList<>();

    //Получение истории просмотра тасков
    @Override
    public List<String> getHistory() {
        return new ArrayList<>(history);
    }

    //Метод добавления данных в историю
    @Override
    public void add(Task task) {
        String historyData = "";

        SimpleDateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy HH:mm: ");

        historyData += dateformat.format(new Date()) +
                "ID:" + task.getId() + " " +
                "Type:" + task.getClass().getSimpleName() + " " +
                "Name:" + task.getName();

        history.add(historyData);
    }
}

