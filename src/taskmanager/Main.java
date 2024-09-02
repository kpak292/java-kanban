package taskmanager;


import taskmanager.controller.Managers;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.service.TaskManager;
import taskmanager.service.implementation.FileBackedTaskManager;
import taskmanager.service.implementation.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Main {


    public static void main(String[] args) throws Exception {

        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Task1", "Description3");

        task1.setStartTime(LocalDateTime.of(2024,1,20,13,10,10));

        task1.setDuration(Duration.ofMinutes(100));

        System.out.println(task1.getStartTime());
        System.out.println(task1.getDuration());
        System.out.println(task1.getEndTime());

        manager.addTask(task1);

        System.out.println(manager.getAllTasks().getFirst());

    }
}
