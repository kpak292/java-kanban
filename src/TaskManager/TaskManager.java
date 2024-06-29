package TaskManager;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<Integer, Task>();
    protected HashMap<Integer, Task> epics = new HashMap<Integer, Task>();
    protected HashMap<Integer, Task> subtasks = new HashMap<Integer, Task>();

    private static int idCounter = 1;

    //Получение списка задач по типу
    public ArrayList<Task> getTasksByType(TaskType type) {
        ArrayList<Task> result = null;

        switch (type) {
            case TASK -> {
                result = new ArrayList<>(tasks.values());
            }
            case EPIC -> {
                result = new ArrayList<>(epics.values());
            }
            case SUBTASK -> {
                result = new ArrayList<>(subtasks.values());
            }
        }

        return result;
    }

    //Получение списка всех задач
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subtasks.values());

        return allTasks;
    }

    //Добавление задачи в менеджер
    // *Подзадача, эпик которой отсутствует в менеджере, не будет добавлена
    public void addTask(Task task) {
        task.setTaskID(idCounter);
        boolean isAdded = true;

        if (task instanceof Epic) {
            epics.put(idCounter, task);
        } else if (task instanceof Subtask) {
            Subtask subTask = (Subtask) task;

            if (epics.containsKey(subTask.getEpicId())) {
                subtasks.put(idCounter, task);
            } else {
                System.out.println("Ошибка: Эпик данной подзадачи не зведен в системе");
                isAdded = false;
            }

        } else {
            tasks.put(idCounter, task);
        }

        if (isAdded) {
            idCounter++;
        }
    }

    //Удаление списка задач по типу
    //*При удалении всех эпиков, сразу же удаляются все подзадачи
    public void deleteTasksByType(TaskType type) {
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
                    updateEpicStatus(task.getTaskID());
                }
            }
        }
    }

    //Удаление всех задач
    public void deleteAllTasks() {
        deleteTasksByType(TaskType.TASK);
        deleteTasksByType(TaskType.EPIC);
    }

    //Получение задачи по ID
    //*Если такой задачи нет, то вернет NUll
    public Task getTaskById(int id) {
        Task result = null;

        if (tasks.containsKey(id)) {
            result = tasks.get(id);
        } else if (epics.containsKey(id)) {
            result = epics.get(id);
        } else if (subtasks.containsKey(id)) {
            result = subtasks.get(id);
        }else{
            System.out.println("Ошибка: задача "+id+" не найдена");
        }

        return result;
    }

    //Удаление задачи по ID
    //*При удалении эпика удаляются все подзадачи
    public void deleteTaskById(int id) {
        Task task = null;

        if (tasks.containsKey(id)) {
            task = tasks.remove(id);
        } else if (epics.containsKey(id)) {
            task = epics.get(id);
            epics.remove(id);

            //Удаление всех подзадач привязанных к эпику
            ArrayList<Task> taskArray = getSubtasksByEpic((Epic) task);
            for (Task taskToCheck : taskArray) {
                subtasks.remove(taskToCheck.getTaskID());
            }

        } else if (subtasks.containsKey(id)) {
            task = subtasks.remove(id);
        }

        if (task == null) {
            System.out.println("Ошибка: Задача для удаления не найдена");
        }
    }

    //Получение всех подзадач эпика
    public ArrayList<Task> getSubtasksByEpic(Epic epic){
        ArrayList<Task> selectedSubtasks = new ArrayList<>();

        for (Task task:subtasks.values()){
            Subtask subtask = (Subtask) task;
            if(subtask.getEpicId() == epic.getTaskID()){
                selectedSubtasks.add(subtask);
            }
        }

        return selectedSubtasks;
    }

    //Обновление статуса задачи
    //*ИД устанавливается только при добавлении объекта в менеджер, соответственно если ИД == 0 то выдаем ошибку
    //*Так же проверяем присутствие номера задачи в массиве, тк может быть передан объект из другого менеджера
    public void updateTask(Task task){
        if (task.getTaskID() == 0) {
            System.out.println("Ошибка: Новую задачу неоходимо завести через метод addTask");
            return;
        }


        if (task instanceof Subtask&&subtasks.containsKey(task.getTaskID())){
            subtasks.put(task.getTaskID(),task);
            Subtask subtask = (Subtask) task;
            updateEpicStatus(subtask.getEpicId());
        }else if(task instanceof Epic && epics.containsKey(task.getTaskID())) {
            epics.put(task.getTaskID(), task);
        }else if(tasks.containsKey(task.taskID)){
            tasks.put(task.getTaskID(),task);
        }else{
            System.out.println("Ошибка: Данной задачи нет в менеджере");
        }

    }

    //Обновление Эпика в зависимости от статуса подзадач
    private void updateEpicStatus(int epicID){
        int newCount =0;
        int inProgressCount = 0;
        int doneCount = 0;
        Epic epic = (Epic) epics.get(epicID);

        ArrayList<Task> taskArray = getSubtasksByEpic(epic);

        for (Task task:taskArray){
            TaskStatus status = task.getTaskStatus();

            switch (status){
                case TaskStatus.NEW -> newCount++;
                case TaskStatus.IN_PROGRESS -> inProgressCount++;
                case TaskStatus.DONE -> doneCount++;
            }
        }

        if (newCount==taskArray.size()){
            epic.updateStatus(TaskStatus.NEW);
        }else if(doneCount==taskArray.size()){
            epic.updateStatus(TaskStatus.DONE);
        }else {
            epic.updateStatus(TaskStatus.IN_PROGRESS);
        }

    }

    //Для тестирования клонирования
    public Task taskClone(Task task){
        Task clone;

        if( task instanceof Epic){
            clone = new Epic(task.getTaskName(),task.getTaskDescription());
        }else if(task instanceof Subtask){
            clone = new Subtask(task.getTaskName(),task.getTaskDescription(),((Subtask) task).getEpicId());
        }else{
            clone = new Task(task.getTaskName(),task.getTaskDescription());
        }

        clone.taskID = task.getTaskID();

        return clone;
    }
}
