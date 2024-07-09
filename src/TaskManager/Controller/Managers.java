package TaskManager.Controller;

import TaskManager.Service.HistoryManager;
import TaskManager.Service.Implementation.InMemoryHistoryManager;
import TaskManager.Service.Implementation.InMemoryTaskManager;
import TaskManager.Service.TaskManager;

public class Managers {
    private static InMemoryTaskManager inMemoryTaskManager;
    private static InMemoryHistoryManager inMemoryHistoryManager;


    public static TaskManager getDefault(){
        if (inMemoryTaskManager==null){
            inMemoryTaskManager= new InMemoryTaskManager();
        }

        return inMemoryTaskManager;
    }

    public static HistoryManager getDefaultHistory(){
        if (inMemoryHistoryManager==null){
            inMemoryHistoryManager= new InMemoryHistoryManager();
        }

        return inMemoryHistoryManager;
    }
}
