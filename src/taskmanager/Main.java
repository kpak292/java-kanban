package taskmanager;


import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.service.implementation.FileBackedTaskManager;

public class Main {


    public static void main(String[] args) throws Exception {
        FileBackedTaskManager manager = new FileBackedTaskManager();

        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");

        manager.addTask(epic1);//1
        manager.addTask(epic2);//2

        Task task1 = new Task("Task1", "Description3");
        Task task2 = new Task("Task2", "Description4");

        manager.addTask(task1);//3
        manager.addTask(task2);//4

        Subtask subTask1 = new Subtask("Subtask1", "Description5", epic1.getId());
        Subtask subTask2 = new Subtask("Subtask2", "Description6", epic2.getId());
        Subtask subTask3 = new Subtask("Subtask3", "Description7", epic2.getId());

        manager.addTask(subTask1);//5
        manager.addTask(subTask2);//6
        manager.addTask(subTask3);//7


    }
}
