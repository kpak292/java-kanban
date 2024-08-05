import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.model.enums.Status;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
    Task task;
    Task task1;
    Subtask subtask;
    Epic epic;

    @BeforeEach
    public void dataCreation() {
        String name = "name";
        String description = "description";

        task = new Task(name, description);
        task.setId(1);
        task.setStatus(Status.DONE);

        task1 = new Task("name", "description");
        task1.setId(1);
        task1.setStatus(Status.DONE);

        subtask = new Subtask(name, description, 10);
        subtask.setId(2);
        subtask.setStatus(Status.IN_PROGRESS);

        epic = new Epic(name, description);
        epic.setId(10);
        epic.setStatus(Status.NEW);
        epic.getSubtaskIds().add(2);
    }

    @Test
    public void shouldBeTaskWithCorrectParams() {
        assertEquals(1, task.getId());
        assertEquals("name", task.getName());
        assertEquals(Status.DONE, task.getStatus());
        assertEquals("description", task.getDescription());
    }

    @Test
    public void shouldBeEpicWithCorrectParams() {
        assertEquals(10, epic.getId());
        assertEquals("name", epic.getName());
        assertEquals("description", epic.getDescription());
        assertEquals(Status.NEW, epic.getStatus());
        assertArrayEquals(new Integer[]{2}, epic.getSubtaskIds().toArray());
    }

    @Test
    public void shouldBeSubtaskWithCorrectParams() {
        assertEquals(2, subtask.getId());
        assertEquals("name", subtask.getName());
        assertEquals("description", subtask.getDescription());
        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
        assertEquals(10, subtask.getEpicId());
    }

    @Test
    public void shouldBeEqualIfSameParams() {
        assertEquals(task1, task);
    }

    @Test
    public void shouldNotBeEqualIfDifferentName() {
        task1.setName("name1");

        assertNotEquals(task, task1);
    }

    @Test
    public void shouldNotBeEqualIfDifferentDescription() {
        task1.setDescription("description1");

        assertNotEquals(task, task1);
    }

    @Test
    public void shouldNotBeEqualIfDifferentStatus() {
        task1.setStatus(Status.NEW);

        assertNotEquals(task, task1);
    }

    @Test
    public void shouldNotBeEqualIfDifferentID() {
        task1.setId(2);

        assertNotEquals(task, task1);
    }

    @Test
    public void shouldNotBeEqualIfDifferentType() {
        task.setStatus(Status.NEW);
        epic.setId(1);

        assertNotEquals(task, epic);
    }

    @Test
    public void shouldBeEpicEqualIfSame() {
        Epic epic1 = new Epic("name", "description");
        epic1.setId(10);
        epic1.setStatus(Status.NEW);
        epic1.getSubtaskIds().add(2);

        assertEquals(epic1, epic);
    }

    @Test
    public void shouldNotBeEpicEqualIfDifferentSubtasks() {
        Epic epic1 = new Epic("name", "description");
        epic1.setId(10);
        epic1.setStatus(Status.NEW);
        epic1.getSubtaskIds().add(2);
        epic1.getSubtaskIds().add(3);

        assertNotEquals(epic1, epic);
    }

    @Test
    public void shouldBeSubtaskEqualIfSame() {
        Subtask subtask1 = new Subtask("name", "description", 10);
        subtask1.setId(2);
        subtask1.setStatus(Status.IN_PROGRESS);

        assertEquals(subtask1, subtask);
    }

    @Test
    public void shouldBeSubtaskNotEqualIfDifferentEpic() {
        Subtask subtask1 = new Subtask("name", "description", 11);
        subtask1.setId(2);
        subtask1.setStatus(Status.IN_PROGRESS);

        assertNotEquals(subtask1, subtask);
    }

}