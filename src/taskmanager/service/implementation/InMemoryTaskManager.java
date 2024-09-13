package taskmanager.service.implementation;

import taskmanager.exceptions.TaskOverlapException;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.model.enums.Status;
import taskmanager.model.enums.Type;
import taskmanager.service.HistoryManager;
import taskmanager.service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks = new HashMap<Integer, Task>();
    protected Map<Integer, Task> epics = new HashMap<Integer, Task>();
    protected Map<Integer, Task> subtasks = new HashMap<Integer, Task>();

    protected Set<Task> sortedList = new TreeSet<>(new Comparator<Task>() {
        @Override
        public int compare(Task o1, Task o2) {
            return o1.getStartTime().compareTo(o2.getStartTime());
        }
    });

    HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    protected int idCounter = 1;

    //Получение списка задач по типу
    @Override
    public List<Task> getTasksByType(Type type) {
        List<Task> source;

        switch (type) {
            case EPIC -> {
                source = new ArrayList<>(epics.values());
            }
            case SUBTASK -> {
                source = new ArrayList<>(subtasks.values());
            }
            default -> {
                source = new ArrayList<>(tasks.values());
            }
        }

        return source.stream().map(Task::clone).toList();
    }

    //Получение списка всех задач
    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subtasks.values());

        return allTasks.stream().map(Task::clone).toList();
    }

    //Добавление задачи в менеджер
    // *Подзадача, эпик которой отсутствует в менеджере, не будет добавлена, метод вернет -1
    // *Если Задача или подзадача накладываются на сроки существующих задач вернет -2
    // *Успешное добавление вернет ИД задачи
    // *Задачи без начала не будут добавленны в сортированный список
    @Override
    public int addTask(Task task) {
        task.setId(idCounter);
        task.setStatus(Status.NEW);

        if (task instanceof Epic) {
            epics.put(idCounter, task);
            updateEpicStatus(idCounter);
        } else if (task instanceof Subtask subtask) {

            if (!epics.containsKey(subtask.getEpicId())) {
                return -1;
            }

            try {
                checkTasksOverlap(subtask);
            } catch (TaskOverlapException e) {
                System.out.println(e.getMessage());
                return -2;
            }

            subtasks.put(idCounter, subtask);
            updateEpicStatus(subtask.getEpicId());
        } else {

            try {
                checkTasksOverlap(task);
            } catch (TaskOverlapException e) {
                System.out.println(e.getMessage());
                return -2;
            }

            tasks.put(idCounter, task);
        }

        return idCounter++;
    }

    //Удаление списка задач по типу
//*При удалении всех эпиков, сразу же удаляются все подзадачи
    @Override
    public void deleteTasksByType(Type type) {
        switch (type) {
            case TASK -> {
                tasks.values().forEach(task -> {
                    historyManager.remove(task.getId());
                    removeFromSortedList(task);
                });

                tasks.clear();
            }

            case EPIC -> {
                epics.values().forEach(task -> {
                    historyManager.remove(task.getId());
                    removeFromSortedList(task);
                });

                subtasks.values().forEach(task -> {
                    historyManager.remove(task.getId());
                    removeFromSortedList(task);
                });

                epics.clear();
                subtasks.clear();
            }
            case SUBTASK -> {
                subtasks.values().forEach(task -> {
                    historyManager.remove(task.getId());
                    removeFromSortedList(task);
                });

                subtasks.clear();

                //Обновление статусов всех эпиков
                epics.values().forEach(task -> updateEpicStatus(task.getId()));
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
    public Optional<Task> getTaskById(int id) {
        Task result;

        if (tasks.containsKey(id)) {
            result = tasks.get(id).clone();
        } else if (epics.containsKey(id)) {
            result = epics.get(id).clone();
        } else if (subtasks.containsKey(id)) {
            result = subtasks.get(id).clone();
        } else {
            return Optional.empty();
        }

        historyManager.add(result);

        return Optional.of(result);
    }

    //Удаление задачи по ID
//*При удалении эпика удаляются все подзадачи
//*При удалении подзадачи пересчитывается статус Эпика
//*Необходимо в будущем добавить exception (тот же что и для метода getTaskById)
    @Override
    public int deleteTaskById(int id) {
        Task task = null;

        if (tasks.containsKey(id)) {
            task = tasks.remove(id);
            removeFromSortedList(task);
        } else if (epics.containsKey(id)) {
            task = epics.remove(id);

            //Удаление всех подзадач привязанных к эпику
            getSubtasksByEpicId(id).forEach(taskToDelete -> {
                Task subtask = subtasks.remove(taskToDelete.getId());
                removeFromSortedList(subtask);
                historyManager.remove(taskToDelete.getId());
            });
        } else if (subtasks.containsKey(id)) {
            task = subtasks.remove(id);
            removeFromSortedList(task);

            Subtask subtask = (Subtask) task;
            updateEpicStatus(subtask.getEpicId());
        }

        historyManager.remove(id);

        if (task == null) {
            return -1;
        } else {
            return 0;
        }
    }

    //Получение всех подзадач эпика
    public List<Task> getSubtasksByEpicId(int epicId) {
        return subtasks.values().stream()
                .filter(task -> ((Subtask) task).getEpicId() == epicId)
                .map(task -> ((Subtask) task).clone())
                .toList();
    }

    //Обновление статуса задачи
//*Так же проверяем присутствие номера задачи в массиве, тк может быть передан объект из другого менеджера
//*Необходимо в будущем добавить exception (тот же что и для метода getTaskById)
    @Override
    public int updateTask(Task task) {
        int result = 0;
        if (task instanceof Subtask subtask &&
                subtasks.containsKey(task.getId()) &&
                epics.containsKey(subtask.getEpicId())) {

            try {
                checkTasksOverlap(subtask);
            } catch (TaskOverlapException e) {
                System.out.println(e.getMessage());
                result = 1;
            }

            subtasks.put(task.getId(), task);
            updateEpicStatus(subtask.getEpicId());
        } else if (task instanceof Epic && epics.containsKey(task.getId())) {
            epics.put(task.getId(), task);
            updateEpicStatus(task.getId());
        } else if (tasks.containsKey(task.getId())) {

            try {
                checkTasksOverlap(task);
            } catch (TaskOverlapException e) {
                System.out.println(e.getMessage());
                result = 1;
            }

            tasks.put(task.getId(), task);
        } else {
            result = -1;
        }

        return result;
    }

    //Обновление Эпика в зависимости от статуса подзадач
//*Необходимо в будущем добавить exception (тот же что и для метода getTaskById)
    private void updateEpicStatus(int epicID) {
        int newCount = 0;
        int inProgressCount = 0;
        int doneCount = 0;

        LocalDateTime start = LocalDateTime.MAX;
        LocalDateTime end = LocalDateTime.MIN;

        if (!epics.containsKey(epicID)) {
            System.out.println("Error: Epic " + epicID + "is not found");
            return;
        }

        Epic epic = (Epic) epics.get(epicID);

        List<Task> taskArray = getSubtasksByEpicId(epicID);
        List<Integer> epicSubtasks = epic.getSubtaskIds();
        epicSubtasks.clear();

        for (Task task : taskArray) {
            Status status = task.getStatus();
            epicSubtasks.add(task.getId());

            switch (status) {
                case Status.NEW -> newCount++;
                case Status.IN_PROGRESS -> inProgressCount++;
                case Status.DONE -> doneCount++;
            }

            if (task.getStartTime() != null && task.getStartTime().isBefore(start)) start = task.getStartTime();
            if (task.getStartTime() != null && task.getEndTime().isAfter(end)) end = task.getEndTime();
        }

        if (newCount == taskArray.size()) {
            epic.setStatus(Status.NEW);
        } else if (doneCount == taskArray.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        if (start != LocalDateTime.MAX) {
            epic.setStartTime(start);
            epic.setDuration(Duration.between(start, end));
        }

    }

    @Override
    public void restartCounter() {
        this.idCounter = 1;
    }

    private boolean isOverlapping(Task o1, Task o2) {
        if (o1.getStartTime() == null || o2.getStartTime() == null) {
            throw new IllegalStateException("Tasks with null startTime can't be checked");
        }

        return (!o1.getStartTime().isBefore(o2.getStartTime()) || !o1.getEndTime().isBefore(o2.getStartTime())) &&
                (!o2.getStartTime().isBefore(o1.getStartTime()) || !o2.getEndTime().isBefore(o1.getStartTime()));
    }

    public void checkTasksOverlap(Task task) {
        if (task.getStartTime() == null) {
            removeFromSortedList(task);
            return;
        }


        if (!sortedList.stream()
                .filter(innerTask -> innerTask.getId() != task.getId())
                .anyMatch(innerTask -> isOverlapping(innerTask, task))) {
            sortedList.add(task);
        } else {
            throw new TaskOverlapException("Error: Task startTime and Duration is overlapping current tasks");
        }
    }

    private void removeFromSortedList(Task task) {
        sortedList.removeIf(innerTask -> innerTask.getId() == task.getId());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return sortedList.stream()
                .map(Task::clone)
                .toList();
    }

    @Override
    public List<String> getHistory() {
        return historyManager.getHistory();
    }

}


