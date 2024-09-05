import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.controller.Managers;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.model.enums.Status;
import taskmanager.model.enums.Type;
import taskmanager.service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {
    TaskManager manager = Managers.getDefault();

    @BeforeEach
    public void dataPreparation() {
        manager.restartCounter();
        manager.deleteAllTasks();

        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");

        manager.addTask(epic1);//1
        manager.addTask(epic2);//2

        Task task1 = new Task("Task1", "Description3");
        Task task2 = new Task("Task2", "Description4");

        LocalDateTime start1 = LocalDateTime.of(2024, 1, 1, 9, 00);
        Duration duration = Duration.ofMinutes(90);

        task1.setStartTime(start1);
        task1.setDuration(duration);

        task2.setStartTime(start1.plusHours(2));
        task2.setDuration(duration);

        manager.addTask(task1);//3
        manager.addTask(task2);//4

        Subtask subTask1 = new Subtask("Subtask1", "Description5", epic1.getId());
        Subtask subTask2 = new Subtask("Subtask2", "Description6", epic2.getId());
        Subtask subTask3 = new Subtask("Subtask3", "Description7", epic2.getId());

        subTask1.setStartTime(start1.plusHours(4));
        subTask1.setDuration(duration);

        subTask2.setStartTime(start1.plusHours(6));
        subTask2.setDuration(duration);

        subTask3.setStartTime(start1.plusHours(8));
        subTask3.setDuration(duration);

        manager.addTask(subTask1);//5
        manager.addTask(subTask2);//6
        manager.addTask(subTask3);//7
    }

    //Add Tasks
    @Test
    public void shouldNotAddSubtaskWithoutEpic() {
        Subtask subtask = new Subtask("a", "b", 3);

        assertTrue(manager.addTask(subtask) < 0);
    }

    @Test
    public void shouldAssignIdCorrectly() {
        Task taskNew = new Task("nameNew", "desc");
        taskNew.setId(100);

        manager.addTask(taskNew);

        assertTrue(manager.getTaskById(100).isEmpty());
        assertEquals(8, manager.getTaskById(8).get().getId());
    }

    @Test
    public void shouldAddTaskCorrectly() {
        Task task = new Task("Task2", "Description4");
        task.setId(4);

        assertEquals(manager.getTaskById(4).get(), task);
    }

    @Test
    public void shouldAssignNewStatusAtAdding() {
        Task task = new Task("a", "b");
        task.setStatus(Status.DONE);

        int taskid = manager.addTask(task);

        assertEquals(Status.NEW, manager.getTaskById(taskid).get().getStatus());
    }

    //Retrieve Data
    @Test
    public void shouldRetrieveCorrectData() {
        assertEquals(2, manager.getTasksByType(Type.TASK).size());
        assertEquals(3, manager.getTasksByType(Type.SUBTASK).size());
        assertEquals(2, manager.getTasksByType(Type.EPIC).size());
        assertEquals(7, manager.getAllTasks().size());
    }

    @Test
    public void shouldNotChangeData() {
        Task task = manager.getTasksByType(Type.TASK).getFirst();
        task.setDescription("UpdatedDescription");
        int id = task.getId();
        assertEquals("Description3", manager.getTaskById(id).get().getDescription());

        Task subtask = manager.getTasksByType(Type.SUBTASK).getFirst();
        subtask.setDescription("UpdatedDescription");
        id = subtask.getId();
        assertEquals("Description5", manager.getTaskById(id).get().getDescription());

        Task epic = manager.getTasksByType(Type.EPIC).getFirst();
        subtask.setDescription("UpdatedDescription");
        id = epic.getId();
        assertEquals("Description1", manager.getTaskById(id).get().getDescription());
    }

    @Test
    public void shouldRetrieveAllsSubtaskOfEpic() {
        Epic epic = (Epic) manager.getTaskById(2).get();

        assertArrayEquals(Arrays.asList(6, 7).toArray(), epic.getSubtaskIds().toArray());
    }

    @Test
    public void shouldNotChangeSubtasksOfEpic() {
        Epic epic = (Epic) manager.getTaskById(2).get();
        assertEquals(2, epic.getSubtaskIds().size());

        epic.getSubtaskIds().add(2);
        epic.getSubtaskIds().add(3);

        epic = (Epic) manager.getTaskById(2).get();
        assertEquals(2, epic.getSubtaskIds().size());
    }

    //Delete Data
    @Test
    public void shouldDeleteAllData() {
        manager.deleteAllTasks();

        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void shouldDeleteCategory() {
        assertEquals(7, manager.getAllTasks().size());

        manager.deleteTasksByType(Type.TASK);
        assertEquals(0, manager.getTasksByType(Type.TASK).size());

        manager.deleteTasksByType(Type.SUBTASK);
        assertEquals(0, manager.getTasksByType(Type.SUBTASK).size());

        manager.deleteTasksByType(Type.EPIC);
        assertEquals(0, manager.getTasksByType(Type.EPIC).size());

        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void shouldDeleteAllSubtasksWhileEpicDeleted() {
        manager.deleteTasksByType(Type.EPIC);

        assertEquals(0, manager.getTasksByType(Type.EPIC).size());
        assertEquals(0, manager.getTasksByType(Type.SUBTASK).size());
    }

    @Test
    public void shouldDeleteTaskByID() {
        assertNotNull(manager.getTaskById(2));

        manager.deleteTaskById(2);

        assertTrue(manager.getTaskById(2).isEmpty());
    }

    @Test
    public void shouldNotAffectWhileDeleteIncorrectID() {
        assertEquals(manager.getAllTasks().size(), 7);

        manager.deleteTaskById(22);

        assertEquals(manager.getAllTasks().size(), 7);
    }

    @Test
    public void shouldDeleteSubtaskAndEpicInternalLink() {
        Epic epic = (Epic) manager.getTaskById(2).get();

        assertEquals(2, epic.getSubtaskIds().size());

        manager.deleteTaskById(6);

        epic = (Epic) manager.getTaskById(2).get();
        assertEquals(1, epic.getSubtaskIds().size());
    }

    @Test
    public void shouldDeleteSubtasksWhileEpicDeleted() {
        assertEquals(3, manager.getTasksByType(Type.SUBTASK).size());

        manager.deleteTaskById(2);

        assertEquals(1, manager.getTasksByType(Type.SUBTASK).size());
    }

    //Update data
    @Test
    public void shouldChangeDataOfTask() {
        String name = "nameNew";
        String description = "descriptionNew";
        Status status = Status.IN_PROGRESS;

        Task task = new Task(name, description);
        task.setId(3);
        task.setStatus(status);

        manager.updateTask(task);

        Task updatedTask = manager.getTaskById(3).get();

        assertEquals(name, updatedTask.getName());
        assertEquals(description, updatedTask.getDescription());
        assertEquals(status, updatedTask.getStatus());
    }

    @Test
    public void shouldChangeDataOfSubtask() {
        String name = "nameNew";
        String description = "descriptionNew";
        Status status = Status.IN_PROGRESS;

        Subtask task = new Subtask(name, description, 1);
        task.setId(5);
        task.setStatus(status);

        manager.updateTask(task);

        Subtask updatedTask = (Subtask) manager.getTaskById(5).get();

        assertEquals(name, updatedTask.getName());
        assertEquals(description, updatedTask.getDescription());
        assertEquals(status, updatedTask.getStatus());
    }

    @Test
    public void shouldChangeDataOfEpic() {
        String name = "nameNew";
        String description = "descriptionNew";
        Status status = Status.IN_PROGRESS;

        Epic task = new Epic(name, description);
        task.setId(2);
        task.setStatus(status);

        manager.updateTask(task);

        Epic updatedTask = (Epic) manager.getTaskById(2).get();

        assertEquals(name, updatedTask.getName());
        assertEquals(description, updatedTask.getDescription());
        assertEquals(Status.NEW, updatedTask.getStatus());

        Subtask subtask = new Subtask(name, description, 2);
        subtask.setId(6);
        subtask.setStatus(Status.DONE);

        manager.updateTask(subtask);

        updatedTask = (Epic) manager.getTaskById(2).get();
        assertEquals(Status.IN_PROGRESS, updatedTask.getStatus());

        Subtask subtask1 = new Subtask(name, description, 2);
        subtask1.setId(7);
        subtask1.setStatus(Status.DONE);

        manager.updateTask(subtask1);

        updatedTask = (Epic) manager.getTaskById(2).get();
        assertEquals(Status.DONE, updatedTask.getStatus());

        manager.deleteTasksByType(Type.SUBTASK);

        updatedTask = (Epic) manager.getTaskById(2).get();
        assertEquals(Status.NEW, updatedTask.getStatus());
    }

    //History Check
    @Test
    public void shouldGetCorrectHistory() {
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getTaskById(1);
        manager.getTaskById(1);
        Managers.getDefaultHistory().remove(2);
        manager.getTaskById(2);
        manager.getTaskById(3);
        manager.getTaskById(4);

        List<String> log = Managers.getDefaultHistory().getHistory();
        int expected = 1;
        for (String entry : log) {
            assertTrue(entry.matches("ID:" + expected++ + ".*"));
        }
    }

    @Test
    public void shouldRefuseToAddOverlappingTask() {
        int tasks = manager.getAllTasks().size();
        Task overlap = new Task("Overlap", "Description");

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 31);
        Duration duration = Duration.ofMinutes(60);

        overlap.setStartTime(start);
        overlap.setDuration(duration);

        assertTrue(manager.addTask(overlap) < 0);
        assertEquals(tasks, manager.getAllTasks().size());
    }

    @Test
    public void shouldNotAddTasksInPrioritizedList() {
        int amountOfPrioritized = manager.getPrioritizedTasks().size();
        int amountOfTotal = manager.getAllTasks().size();

        Task task = new Task("Test", "Test0");

        manager.addTask(task);

        assertTrue(manager.getAllTasks().size() - amountOfTotal == 1);
        assertTrue(manager.getPrioritizedTasks().size() == amountOfPrioritized);
    }

    @Test
    public void shouldDeleteTaskIfDeletedStartDate() {
        int amountOfPrioritized = manager.getPrioritizedTasks().size();
        int amountOfTotal = manager.getAllTasks().size();

        Task task = manager.getTaskById(3).get();
        task.setStartTime(null);

        manager.updateTask(task);

        assertTrue(manager.getAllTasks().size() == amountOfTotal);
        assertTrue(manager.getPrioritizedTasks().size() < amountOfPrioritized);
    }

    @Test
    public void shouldCorrectlyReturnPrioritizedList() {
        Task task1 = new Task("Test", "Test");

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 40);
        Duration duration = Duration.ofMinutes(10);

        task1.setStartTime(start);
        task1.setDuration(duration);

        int id = manager.addTask(task1);//3

        List<Task> prioritized = manager.getPrioritizedTasks();

        assertTrue(prioritized.get(1).getId() == id);
    }

    @Test
    public void shouldCorrectlyCaculateEpicStartandDuration() {
        assertEquals(manager.getTaskById(1).get().getStartTime(), manager.getTaskById(5).get().getStartTime());
        assertEquals(manager.getTaskById(1).get().getDuration(), manager.getTaskById(5).get().getDuration());

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 9, 00);
        Duration duration = Duration.ofMinutes(90);

        Subtask subTaskEarly = new Subtask("Subtask", "Early", 1);
        subTaskEarly.setStartTime(start.minusHours(2));
        subTaskEarly.setDuration(duration);

        int earlySubtaskId = manager.addTask(subTaskEarly);

        assertEquals(manager.getTaskById(1).get().getStartTime(),
                manager.getTaskById(earlySubtaskId).get().getStartTime());

        Subtask subTaskLater = new Subtask("Subtask", "Later", 1);
        subTaskLater.setStartTime(start.plusHours(10));
        subTaskLater.setDuration(duration);

        int laterSubtaskId = manager.addTask(subTaskLater);

        assertEquals(manager.getTaskById(1).get().getEndTime(), manager.getTaskById(laterSubtaskId).get().getEndTime());
    }

}
