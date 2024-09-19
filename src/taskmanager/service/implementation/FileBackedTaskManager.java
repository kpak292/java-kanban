package taskmanager.service.implementation;

import taskmanager.controller.Managers;
import taskmanager.exceptions.ManagerLoadException;
import taskmanager.exceptions.ManagerSaveException;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.model.enums.Status;
import taskmanager.model.enums.Type;
import taskmanager.service.HistoryManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;
    private String header = "\"ID\";" +
            "\"Type\";" +
            "\"Name\";" +
            "\"Description\";" +
            "\"Status\";" +
            "\"StartDate\";" +
            "\"Duration\";" +
            "\"EndDate\";" +
            "\"Subtasks\";" +
            "\"EpicID\";";

    public FileBackedTaskManager() throws ManagerLoadException {
        super(Managers.getDefaultHistory());

        String home = System.getProperty("user.home");
        this.path = Paths.get(home + File.separator + "FBTM_DB.csv");

        createIfNotExists(path);
    }

    public FileBackedTaskManager(Path path) throws ManagerLoadException {
        super(Managers.getDefaultHistory());

        if (!Files.exists(path)) {
            throw new ManagerLoadException("Error: file is not exists");
        }

        this.path = path;

        load();
    }

    public FileBackedTaskManager(Path path, HistoryManager manager) throws ManagerLoadException {
        super(manager);

        this.path = path;
        createIfNotExists(path);
    }

    private void createIfNotExists(Path path) throws ManagerLoadException {
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
                save();
            } catch (IOException e) {
                throw new ManagerLoadException("Error: can't create file " + path.toString());
            }
        }
    }

    public void save() throws ManagerSaveException {
        List<Task> list = super.getAllTasks();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write(header + "\"" + idCounter + "\"");
            writer.newLine();

            for (Task task : list) {
                writer.append(task.toString());
                writer.newLine();
            }


        } catch (IOException e) {
            throw new ManagerSaveException("Error: can't save file " + path.toString());
        }

    }

    public void load() throws ManagerLoadException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String header = reader.readLine();

            if (header == null) {
                return;
            }

            if (!header.matches(this.header + ".*")) {
                throw new ManagerLoadException("Error: data format isn't consistent");
            }

            deleteAllTasks();
            restartCounter();

            header = header.replace(this.header, "")
                    .replaceAll("\"", "");

            int counter = Integer.parseInt(header);

            this.idCounter = counter;

            while (reader.ready()) {
                stringToTask(reader.readLine());
            }


        } catch (IOException e) {
            throw new ManagerLoadException("Error: can't load file " + path.toString());
        } catch (NumberFormatException e) {
            throw new ManagerLoadException("Error: can't convert String into Integer ");
        }
    }

    private void stringToTask(String data) {
        data = data.replaceAll("\"", "");
        String[] dataArray = data.split(";");

        switch (dataArray[1]) {
            case "Epic" -> {
                Epic epic = new Epic(dataArray[2], dataArray[3]);
                int id = Integer.parseInt(dataArray[0]);
                epic.setId(id);

                LocalDateTime start = dataArray[5].isBlank() ? null : LocalDateTime.parse(dataArray[5], Task.formatter);
                long duration = dataArray[6].isBlank() ? 0 : Long.parseLong(dataArray[6]);

                epic.setDuration(Duration.ofMinutes(duration));
                epic.setStartTime(start);

                List<Integer> list = epic.getSubtaskIds();

                String[] subids = dataArray[8]
                        .replace("[", "")
                        .replace("]", "")
                        .split(",");

                for (String subid : subids) {
                    int numericID = Integer.parseInt(subid.trim());
                    list.add(numericID);
                }

                epic.setStatus(stringToStatus(dataArray[4]));

                epics.put(id, epic);
            }
            case "Subtask" -> {
                Subtask subtask = new Subtask(dataArray[2], dataArray[3], Integer.parseInt(dataArray[9]));
                int id = Integer.parseInt(dataArray[0]);
                subtask.setId(id);
                subtask.setStatus(stringToStatus(dataArray[4]));

                LocalDateTime start = dataArray[5].isBlank() ? null : LocalDateTime.parse(dataArray[5], Task.formatter);
                long duration = dataArray[6].isBlank() ? 0 : Long.parseLong(dataArray[6]);

                subtask.setDuration(Duration.ofMinutes(duration));
                subtask.setStartTime(start);


                subtasks.put(id, subtask);
            }
            default -> {
                Task task = new Task(dataArray[2], dataArray[3]);
                int id = Integer.parseInt(dataArray[0]);
                task.setId(id);
                task.setStatus(stringToStatus(dataArray[4]));

                LocalDateTime start = dataArray[5].isBlank() ? null : LocalDateTime.parse(dataArray[5], Task.formatter);
                long duration = dataArray[6].isBlank() ? 0 : Long.parseLong(dataArray[6]);

                task.setDuration(Duration.ofMinutes(duration));
                task.setStartTime(start);

                tasks.put(id, task);
            }
        }
    }

    private Status stringToStatus(String text) {
        Status status;

        switch (text) {
            case "IN_PROGRESS" -> status = Status.IN_PROGRESS;
            case "DONE" -> status = Status.DONE;
            default -> status = Status.NEW;
        }

        return status;
    }

    @Override
    public int addTask(Task task) {
        if (task == null) {
            return -1;
        }

        int result = super.addTask(task);
        save();
        return result;
    }

    @Override
    public void deleteTasksByType(Type type) {
        super.deleteTasksByType(type);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public int deleteTaskById(int id) {
        int result = super.deleteTaskById(id);
        save();
        return result;
    }

    @Override
    public int updateTask(Task task) {
        if (task == null) {
            return -1;
        }

        int result = super.updateTask(task);

        if (result >= 0) save();
        return result;
    }
}
