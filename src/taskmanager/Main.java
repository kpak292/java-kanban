package taskmanager;


import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.model.enums.Status;
import taskmanager.service.implementation.FileBackedTaskManager;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {


    public static void main(String[] args) throws Exception{
        String home = System.getProperty("user.home");
        Path path = Paths.get(home + File.separator + "FBTM_DB.csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(path);

        System.out.println(manager.getSequence());

        for(Task task:manager.getAllTasks()){
            System.out.println(task);
        }
    }
}
