import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.controller.Managers;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.model.enums.Status;
import taskmanager.service.TaskManager;
import taskmanager.service.implementation.FileBackedTaskManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IOTest {
    TaskManager manager;
    Path path;

    @BeforeEach
    public void dataPreparation() throws IOException {
        path = File.createTempFile("test", ".csv").toPath();

        manager = Managers.getFileBacked(path);

        manager.restartCounter();
        manager.deleteAllTasks();

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

    @Test
    public void shouldAddAllTasks() throws IOException {
        List<String> data = new ArrayList<>();
        FileReader fileReader = new FileReader(path.toFile());
        BufferedReader reader = new BufferedReader(fileReader);

        while (reader.ready()) {
            data.add(reader.readLine());
        }

        assertTrue(data.size() == 8);

        Task task = new Task("new Task", "desc");

        manager.addTask(task);

        data.clear();
        fileReader = new FileReader(path.toFile());
        reader = new BufferedReader(fileReader);

        while (reader.ready()) {
            data.add(reader.readLine());
        }

        assertTrue(data.size() == 9);

    }

    @Test
    public void shouldLoadAllTasks() throws IOException {
        FileBackedTaskManager newManager = new FileBackedTaskManager(path);

        List<Task> origin = manager.getAllTasks();
        List<Task> loaded = newManager.getAllTasks();

        assertArrayEquals(origin.toArray(), loaded.toArray());
    }

    @Test
    public void shouldSetUpCorrectSequence() throws IOException {
        FileBackedTaskManager newManager = new FileBackedTaskManager(path);

        Task task = new Task("Sequence Test", "Should assign ID 8");

        int sequence = newManager.addTask(task);

        assertTrue(sequence == 8);
    }

    @Test
    public void shouldSaveChanges() throws IOException {
        Task task = manager.getTaskById(3);
        task.setStatus(Status.DONE);

        manager.updateTask(task);

        List<String> data = new ArrayList<>();
        FileReader fileReader = new FileReader(path.toFile());
        BufferedReader reader = new BufferedReader(fileReader);

        while (reader.ready()) {
            data.add(reader.readLine());
        }

        String result = "\"3\";\"Task\";\"Task1\";\"Description3\";\"DONE\";;";

        assertTrue(data.contains(result));

    }


}
