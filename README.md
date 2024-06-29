# java-kanban
Repository for homework project.
## 1 Классы
Все классы упакованы в пакет TaskManager, для регулирования доступности к параметрам

**TaskManager.Task** - Родительский класс задач с параметрами:
- Название, кратко описывающее суть задачи
- Описание, в котором раскрываются детали.
- Уникальный идентификационный номер задачи, по которому её можно будет найти. При создании у задачи taskId = 0,
сравнение с задачами с id = 0 будет всегда false.
- Статус, отображающий её прогресс, при создании задачи присваивается статус NEW.

**TaskManager.Epic** - Эпик предок задач
- Статус зависит от статуса подзадач
- Если нет подзадач или все задачи со статусом NEW, то статус Эпика NEW
- Если статус всех подзадач DONE, то статус Эпика DONE
- Во всех остальных случаях IN_PROGRESS

**TaskManager.Subtask** - Подзадача предок задач
- Подзадача хранит ИД Эпика
- Подзадача не может быть добавлена в TaskManager.TaskManager, если в нем нет указанного эпика

**TaskManager.TaskManager** - Менеджер задач, который управляет всеми задачами
- Хранит 3 хештаблицы для каждого из типов

## 2 Enum
**TaskManager.TaskStatus** - Хранит статусы задач (New, in progress, done)

**TaskManager.TaskType** - Хранит типы задач (TaskManager.Task, TaskManager.Epic, TaskManager.Subtask)

## 3 Методы работы с TaskManager.TaskManager
**getTasksByType** - Принимает в параметрах TaskManager.TaskType, возвращает ArrayList с задачами этого типа.
- Может дать ошибку NullPointerException, если в параметрах будет передан тип отличный от изначальнух TaskManager.TaskType

**getAllTasks** - Возвращает ArrayList со всеми задачами

**deleteTasksByType** - Принимает в параметрах TaskManager.TaskType, удаляет все задачи по типу
- При удалении всех эпиков, сразу же удаляются все подзадачи
- При удалении всех подзадач, связь с этими подзадачами удаляется у каждого Эпика

**deleteAllTasks** - Удаляет все задачи

**getTaskById** - Принимает в параметрах ID задачи, возвращает задачу 
- Если введен идентификатор несуществуществующей задачи, вернется NUll

**addTask** Принимает в параметрах объект TaskManager.Task и присваивает ему ID
- если добавляется Подзадача, эпик которой отсутствует в менеджере, не будет добавлена, и будет выведено сообщение
в консоль. (Возможно следует бросить исключение в таком случае)

**deleteTaskById** Принимает в параметрах ID задачи для удаления
- При вводе несуществующего номера выводит сообшение об ошибке в консоль
- При удалении эпика удаляются все подзадачи

**getSubtasksByEpic** Принимает в параметрах эпик и возвращает список подзадач