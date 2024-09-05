package taskmanager.service;

import taskmanager.model.Task;
import taskmanager.model.enums.Type;

import java.util.List;
import java.util.Optional;

public interface TaskManager {

    //Получение списка задач по типу
    List<Task> getTasksByType(Type type);

    //Получение списка всех задач
    List<Task> getAllTasks();

    //Получение задачи по ID
    //*Если такой задачи нет, то вернет NUll (в будущем необходимо сделать соответствующий exception)
    Optional<Task> getTaskById(int id);

    //Добавление задачи в менеджер
    // *Подзадача, эпик которой отсутствует в менеджере, не будет добавлена, метод вернет -1
    int addTask(Task task);

    //Удаление списка задач по типу
    //*При удалении всех эпиков, сразу же удаляются все подзадачи
    void deleteTasksByType(Type type);

    //Удаление всех задач
    void deleteAllTasks();

    //Удаление задачи по ID
    //*При удалении эпика удаляются все подзадачи
    //*При удалении подзадачи пересчитывается статус Эпика
    //*Необходимо в будущем добавить exception (тот же что и для метода getTaskById)
    void deleteTaskById(int id);

    //Обновление статуса задачи
    //*Так же проверяем присутствие номера задачи в массиве, тк может быть передан объект из другого менеджера
    //*Необходимо в будущем добавить exception (тот же что и для метода getTaskById)
    void updateTask(Task task);

    //Обнуление счетчика задач
    void restartCounter();

    //Получение приоритетных задач
    List<Task> getPrioritizedTasks();
}
