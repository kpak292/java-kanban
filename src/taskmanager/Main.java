package taskmanager;

import taskmanager.controller.Managers;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.service.TaskManager;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        //Add data
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");

        manager.addTask(epic1);//1
        manager.addTask(epic2);//2

        Task task1 = new Task("Task1", "Description3");
        Task task2 = new Task("Task2", "Description4");

        manager.addTask(task1);//3
        manager.addTask(task2);//4

        Subtask subTask1 = new Subtask("Subtask1", "Description5", epic2.getId());
        Subtask subTask2 = new Subtask("Subtask2", "Description6", epic2.getId());
        Subtask subTask3 = new Subtask("Subtask3", "Description7", epic2.getId());

        manager.addTask(subTask1);//5
        manager.addTask(subTask2);//6
        manager.addTask(subTask3);//7

        //Filling history
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            manager.getTaskById(random.nextInt(7) + 1);
        }

        System.out.println("History log after random get");
        for (String entry : Managers.getDefaultHistory().getHistory()) {
            System.out.println(entry);
        }

        manager.deleteTaskById(3);
        manager.deleteTaskById(2);

        System.out.println("-".repeat(20));
        System.out.println("History log after tasks deleted");
        for (String entry : Managers.getDefaultHistory().getHistory()) {
            System.out.println(entry);
        }
    }
}
