package TaskManager.Service.Implementation;

import TaskManager.Controller.Managers;
import TaskManager.Model.Commons.Status;
import TaskManager.Model.Commons.Type;
import TaskManager.Model.Epic;
import TaskManager.Model.Subtask;
import TaskManager.Model.Task;
import TaskManager.Service.HistoryManager;
import TaskManager.Service.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<Integer, Task>();
    private HashMap<Integer, Task> epics = new HashMap<Integer, Task>();
    private HashMap<Integer, Task> subtasks = new HashMap<Integer, Task>();
    HistoryManager historyManager = Managers.getDefaultHistory();

    private int idCounter = 1;

    //Получение списка задач по типу
    @Override
    public ArrayList<Task> getTasksByType(Type type) {
        ArrayList<Task> result;

        switch (type) {
            case EPIC -> {
                result = new ArrayList<>(epics.values());
            }
            case SUBTASK -> {
                result = new ArrayList<>(subtasks.values());
            }
            default -> {
                result = new ArrayList<>(tasks.values());
            }
        }

        return result;
    }

    //Получение списка всех задач
    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subtasks.values());

        return allTasks;
    }

    //Добавление задачи в менеджер
    // *Подзадача, эпик которой отсутствует в менеджере, не будет добавлена, метод вернет -1
    @Override
    public int addTask(Task task) {
        int result = idCounter;
        boolean isAdded = true;

        task.setId(idCounter);
        task.setStatus(Status.NEW);

        if (task instanceof Epic) {
            epics.put(idCounter, task);
            updateEpicStatus(idCounter);
        } else if (task instanceof Subtask subTask) {

            if (epics.containsKey(subTask.getEpicId())) {
                subtasks.put(idCounter, subTask);
                updateEpicStatus(subTask.getEpicId());
            } else {
                System.out.println("Ошибка: Эпик данной подзадачи не зведен в системе");
                result = -1;
                isAdded = false;
            }
        } else {
            tasks.put(idCounter, task);
        }

        if (isAdded) {
            idCounter++;
        }

        return result;
    }

    //Удаление списка задач по типу
    //*При удалении всех эпиков, сразу же удаляются все подзадачи
    @Override
    public void deleteTasksByType(Type type) {
        switch (type) {
            case TASK -> tasks.clear();

            case EPIC -> {
                epics.clear();
                subtasks.clear();
            }

            case SUBTASK -> {
                subtasks.clear();

                //Обновление статусов всех эпиков
                for (Task task : epics.values()) {
                    updateEpicStatus(task.getId());
                }
            }
        }
    }

    //Удаление всех задач
    @Override
    public void deleteAllTasks() {
        deleteTasksByType(Type.TASK);
        deleteTasksByType(Type.EPIC);
    }

    //Получение задачи по ID
    //*Если такой задачи нет, то вернет NUll (в будущем необходимо сделать соответствующий exception)
    @Override
    public Task getTaskById(int id) {
        Task result = null;

        if (tasks.containsKey(id)) {
            result = tasks.get(id);
        } else if (epics.containsKey(id)) {
            result = epics.get(id);
        } else if (subtasks.containsKey(id)) {
            result = subtasks.get(id);
        } else {
            System.out.println("Ошибка: задача " + id + " не найдена");
        }

        if (result != null) {
            historyManager.add(result);
        }

        return result;
    }

    //Удаление задачи по ID
    //*При удалении эпика удаляются все подзадачи
    //*При удалении подзадачи пересчитывается статус Эпика
    //*Необходимо в будущем добавить exception (тот же что и для метода getTaskById)
    @Override
    public void deleteTaskById(int id) {
        Task task = null;

        if (tasks.containsKey(id)) {
            task = tasks.remove(id);
        } else if (epics.containsKey(id)) {
            task = epics.get(id);

            //Удаление всех подзадач привязанных к эпику
            ArrayList<Task> taskArray = getSubtasksByEpicId(id);
            for (Task taskToDelete : taskArray) {
                subtasks.remove(taskToDelete.getId());
            }

            //Удаление самого эпика
            epics.remove(id);

        } else if (subtasks.containsKey(id)) {
            task = subtasks.remove(id);
            Subtask subtask = (Subtask) task;
            updateEpicStatus(subtask.getEpicId());
        }

        if (task == null) {
            System.out.println("Ошибка: Задача для удаления не найдена");
        }
    }

    //Получение всех подзадач эпика
    public ArrayList<Task> getSubtasksByEpicId(int epicId) {
        ArrayList<Task> selectedSubtasks = new ArrayList<>();

        for (Task task : subtasks.values()) {
            Subtask subtask = (Subtask) task;

            if (subtask.getEpicId() == epicId) {
                selectedSubtasks.add(subtask);
            }
        }

        return selectedSubtasks;
    }

    //Обновление статуса задачи
    //*Так же проверяем присутствие номера задачи в массиве, тк может быть передан объект из другого менеджера
    //*Необходимо в будущем добавить exception (тот же что и для метода getTaskById)
    @Override
    public void updateTask(Task task) {
        if (task instanceof Subtask subtask && subtasks.containsKey(task.getId())) {
            subtasks.put(task.getId(), task);
            updateEpicStatus(subtask.getEpicId());
        } else if (task instanceof Epic && epics.containsKey(task.getId())) {
            epics.put(task.getId(), task);
            updateEpicStatus(task.getId());
        } else if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Ошибка: Данной задачи нет в менеджере");
        }

    }

    //Обновление Эпика в зависимости от статуса подзадач
    //*Необходимо в будущем добавить exception (тот же что и для метода getTaskById)
    private void updateEpicStatus(int epicID) {
        int newCount = 0;
        int inProgressCount = 0;
        int doneCount = 0;

        if (!epics.containsKey(epicID)) {
            System.out.println("Ошибка: Данного Эпика нет в менеджере");
        }

        Epic epic = (Epic) epics.get(epicID);

        ArrayList<Task> taskArray = getSubtasksByEpicId(epicID);
        ArrayList<Integer> epicSubtasks = epic.getSubtaskIds();
        epicSubtasks.clear();

        for (Task task : taskArray) {
            Status status = task.getStatus();
            epicSubtasks.add(task.getId());

            switch (status) {
                case Status.NEW -> newCount++;
                case Status.IN_PROGRESS -> inProgressCount++;
                case Status.DONE -> doneCount++;
            }
        }

        if (newCount == taskArray.size()) {
            epic.setStatus(Status.NEW);
        } else if (doneCount == taskArray.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

    }

    @Override
    public void restartCounter() {
        this.idCounter = 1;
    }
}


