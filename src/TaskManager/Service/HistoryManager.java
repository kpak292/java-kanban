package TaskManager.Service;

import TaskManager.Model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    public ArrayList<String> getHistory();

    public void add(Task task);
}
