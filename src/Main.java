import TaskManager.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        //Тестирование добавления задачи без Эпика
        System.out.println("1: Проверка добавления подзадачи без Эпика");
        manager.addTask(new Subtask("test","test",0));
        printDelimiter();

        //Добавление тестовых данных
        System.out.println("2: Проверка добавления задач");
        addTestData(manager);
        if (manager.getAllTasks().size()==7){
            System.out.println("Успешно");
        }else {
            System.out.println("Неуспешно");
        }
        printDelimiter();

        //Тестирование отображения списка задач по типам
        System.out.println("3: Проверка получения списка Задач");
        if (manager.getTasksByType(TaskType.TASK).size()==2){
            System.out.println("Успешно");
        }else {
            System.out.println("Неуспешно");
        }
        printDelimiter();

        System.out.println("4: Проверка получения списка Эпиков");
        if (manager.getTasksByType(TaskType.EPIC).size()==2){
            System.out.println("Успешно");
        }else {
            System.out.println("Неуспешно");
        }
        printDelimiter();

        System.out.println("5: Проверка получения списка Подзадач");
        if (manager.getTasksByType(TaskType.SUBTASK).size()==3){
            System.out.println("Успешно");
        }else {
            System.out.println("Неуспешно");
        }
        printDelimiter();

        //Тестирование получения списка всех задач
        System.out.println("6: Проверка получения всех задач");
        if (manager.getAllTasks().size()==7){
            System.out.println("Успешно");
        }else {
            System.out.println("Неуспешно");
        }
        printDelimiter();

        //Тестирование удаления всех задач
        System.out.println("7: Тестирование удаления всех задач");
        manager.deleteAllTasks();
        if (manager.getAllTasks().size()==0){
            System.out.println("Успешно");
        }else {
            System.out.println("Неуспешно");
        }
        printDelimiter();

        //Тестирование удаления категории TaskManager.Task
        addTestData(manager);
        System.out.println("8: Тестирование удаления категории TaskManager.Task");
        manager.deleteTasksByType(TaskType.TASK);
        if (manager.getAllTasks().size()==5){
            System.out.println("Успешно");
        }else {
            System.out.println("Неуспешно");
        }
        manager.deleteAllTasks();
        printDelimiter();

        //Тестирование удаления категории TaskManager.Epic
        addTestData(manager);
        System.out.println("9: Тестирование удаления категории TaskManager.Epic");
        manager.deleteTasksByType(TaskType.EPIC);
        if (manager.getAllTasks().size()==2){
            System.out.println("Успешно");
        }else {
            System.out.println("Неуспешно");
        }
        manager.deleteAllTasks();
        printDelimiter();

        //Тестирование удаления категории SubTask
        addTestData(manager);
        System.out.println("10: Тестирование удаления категории SubTask");
        manager.deleteTasksByType(TaskType.SUBTASK);
        if (manager.getAllTasks().size()==4){
            System.out.println("Успешно");
        }else {
            System.out.println("Неуспешно");
        }
        manager.deleteAllTasks();
        printDelimiter();

        //Тестирование получения задачи по ИД
        addTestData(manager);
        System.out.println("11: Тестируем получения задачи по несуществующему ИД");
        manager.getTaskById(1);
        printDelimiter();

        System.out.println("12: Тестируем получения задачи по ИД 30");
        if (manager.getTaskById(30).getTaskID()==30){
            System.out.println("Успешно");
        }else {
            System.out.println("Неуспешно");
        }
        printDelimiter();

        //Тестируем удаление задачи по ИД
        System.out.println("13: Удаление несуществующего номера");
        manager.deleteTaskById(1);
        printDelimiter();

        System.out.println("14: Тестируем удаление задач по ИД - задача 29");
        manager.deleteTaskById(29);
        manager.getTaskById(29);
        printDelimiter();

        System.out.println("15: Тестируем удаление задач по ИД - Эпик 31");
        manager.deleteTaskById(31);
        manager.getTaskById(31);
        printDelimiter();

        System.out.println("15: Тестируем удаление задач по ИД - Подзадача 35");
        manager.deleteTaskById(35);
        manager.getTaskById(35);
        printDelimiter();
        manager.deleteAllTasks();

        //Тестирование изменения статуса
        addTestData(manager);
        System.out.println("16: Тестируем замену таска со статусом");
        Task task = manager.taskClone(manager.getTaskById(36));
        task.setTaskStatus(TaskStatus.DONE);
        manager.updateTask(task);
        if (manager.getTaskById(36).getTaskStatus()==TaskStatus.DONE){
            System.out.println("Успешно");
        }else {
            System.out.println("Неуспешно");
        }
        printDelimiter();

        System.out.println("17: Тестируем изменение статуса эпика");
        System.out.println(manager.getTaskById(38));
        task = manager.taskClone(manager.getTaskById(38));
        task.setTaskStatus(TaskStatus.DONE);
        printDelimiter();

        System.out.println("18: Тестируем изменения имени и описания эпика");
        task.setTaskName("Epic1 updated");
        task.setTaskDescription("Epic1 updated");
        manager.updateTask(task);
        if (manager.getTaskById(38).getTaskName().equals(manager.getTaskById(38).getTaskDescription())){
            System.out.println("Успешно");
        }else {
            System.out.println("Неуспешно");
        }
        printDelimiter();

        System.out.println("19: Тестируем апдейт статуса Эпика с 1 подзадачей");
        Epic epic1 = (Epic) manager.getTaskById(36);
        task = manager.taskClone(manager.getTaskById(40));
        task.setTaskStatus(TaskStatus.DONE);
        manager.updateTask(task);

        if (epic1.getTaskStatus()==TaskStatus.DONE){
            System.out.println("Успешно");
        }else {
            System.out.println("Неуспешно");
        }
        printDelimiter();

        System.out.println("20: Тестируем апдейт статуса Эпика с 2 подзадачами");
        Epic epic2 = (Epic) manager.getTaskById(37);
        task = manager.taskClone(manager.getTaskById(41));
        Task task2 = manager.taskClone(manager.getTaskById(42));
        task.setTaskStatus(TaskStatus.DONE);
        manager.updateTask(task);
        task2.setTaskStatus(TaskStatus.DONE);
        manager.updateTask(task2);
        if (epic2.getTaskStatus()==TaskStatus.DONE){
            System.out.println("Успешно");
        }else {
            System.out.println("Неуспешно");
        }
        printDelimiter();

    }

    public static void printDelimiter(){
        System.out.println("-".repeat(20));
    }

    public static void printTasks(ArrayList<Task> tasksToPrint){
        if(tasksToPrint.isEmpty()){
            System.out.println("Список задач пуст");
        }

        for(Task task:tasksToPrint){
            System.out.println(task);
        }
    }

    public static void addTestData(TaskManager manager){
        //Создание материала для тестирования
        Task task1 = new Task("Task1","Update test");
        Task task2 = new Task("Task2","Delete test");
        Epic epic1 = new Epic("Epic1","TaskManager.Epic + 1 subtask");
        Epic epic2 = new Epic("Epic2","TaskManager.Epic + 2 subtasks");
        manager.addTask(epic1);
        manager.addTask(epic2);
        Subtask subTask1 = new Subtask("Subtask1", "epic1 test",epic1.getTaskID());
        Subtask subTask2 = new Subtask("Subtask2", "epic2 test",epic2.getTaskID());
        Subtask subTask3 = new Subtask("Subtask3", "epic2 test",epic2.getTaskID());

        //Добавления тасков
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(subTask1);
        manager.addTask(subTask2);
        manager.addTask(subTask3);


    }
}
