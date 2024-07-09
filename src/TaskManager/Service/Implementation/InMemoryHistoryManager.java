package TaskManager.Service.Implementation;

import TaskManager.Model.Task;
import TaskManager.Service.HistoryManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<String> history = new ArrayList<>();
    private static final int HISTORY_LIMIT = 10;

    //Получение истории просмотра тасков
    @Override
    public ArrayList<String> getHistory() {
        return history;
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

