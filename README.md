# java-kanban
Repository for homework project.
## Endpoints

**/tasks** 
Endpoint for work with Taskmanager

**GET**
- /tasks/all - retrieve all tasks as json

<details>
  <summary>Example</summary>

```json
  [
  {
  "ID": 3,
  "Type": "Task",
  "Name": "Description3",
  "Description": "Description3",
  "Status": "NEW",
  "StartTime": "01.01.2024 09:00",
  "Duration": 90
  },
  {
  "ID": 2,
  "Type": "Epic",
  "Name": "Description2",
  "Description": "Description2",
  "Status": "NEW",
  "StartTime": "01.01.2024 15:00",
  "Duration": 210,
  "SubtaskIDs": [
  6,
  7
  ]
  },
  {
  "ID": 7,
  "Type": "Subtask",
  "Name": "Description7",
  "Description": "Description7",
  "Status": "NEW",
  "StartTime": "01.01.2024 17:00",
  "Duration": 90,
  "EpicID": 2
  }
  ]
```

</details>

- /tasks/tasks - retrieve only Task type in Json

<details>
  <summary>Example</summary>

```json
[
  {
    "id": 3,
    "name": "Task1",
    "description": "Description3",
    "status": "NEW",
    "startTime": "01.01.2024 09:00",
    "duration": 90
  },
  {
    "id": 4,
    "name": "Task2",
    "description": "Description4",
    "status": "NEW",
    "startTime": "01.01.2024 11:00",
    "duration": 90
  }
]
```

</details>

- /tasks/epics - retrieve only Epic type in Json

<details>
  <summary>Example</summary>

```json
[
  {
    "ID": 1,
    "Type": "Epic",
    "Name": "Description1",
    "Description": "Description1",
    "Status": "NEW",
    "StartTime": "01.01.2024 13:00",
    "Duration": 90,
    "SubtaskIDs": [
      5
    ]
  },
  {
    "ID": 2,
    "Type": "Epic",
    "Name": "Description2",
    "Description": "Description2",
    "Status": "NEW",
    "StartTime": "01.01.2024 15:00",
    "Duration": 210,
    "SubtaskIDs": [
      6,
      7
    ]
  }
]
```

</details>

- /tasks/subtasks - retrieve only Subtask type in Json

<details>
  <summary>Example</summary>

```json
[
  {
    "ID": 5,
    "Type": "Subtask",
    "Name": "Description5",
    "Description": "Description5",
    "Status": "NEW",
    "StartTime": "01.01.2024 13:00",
    "Duration": 90,
    "EpicID": 1
  },
  {
    "ID": 6,
    "Type": "Subtask",
    "Name": "Description6",
    "Description": "Description6",
    "Status": "NEW",
    "StartTime": "01.01.2024 15:00",
    "Duration": 90,
    "EpicID": 2
  },
  {
    "ID": 7,
    "Type": "Subtask",
    "Name": "Description7",
    "Description": "Description7",
    "Status": "NEW",
    "StartTime": "01.01.2024 17:00",
    "Duration": 90,
    "EpicID": 2
  }
]
```

</details>

- /tasks/[Task ID] - retrieve Task by ID, ID should be positive int

<details>
  <summary>Example 200 SUCCESS</summary>

```json
{
  "ID": 1,
  "Type": "Epic",
  "Name": "Description1",
  "Description": "Description1",
  "Status": "NEW",
  "StartTime": "01.01.2024 13:00",
  "Duration": 90,
  "SubtaskIDs": [
    5
  ]
}
```

</details>

<details>
  <summary>Example 404</summary>

```text
Error: Can't find Task 12
```

</details>

**DELETE**
- /tasks/all - delete all tasks

<details>
  <summary>Example 200 SUCCESS</summary>

```text
SUCCESS
```

</details>

- /tasks/tasks - delete only Task type 

<details>
  <summary>Example 200 SUCCESS</summary>

```text
SUCCESS
```

</details>

- /tasks/subtasks - delete only Subtasks type

<details>
  <summary>Example 200 SUCCESS</summary>

```text
SUCCESS
```

</details>

- /tasks/epics - delete only Epics 

<details>
  <summary>Example 200 SUCCESS</summary>

```text
SUCCESS
```

</details>

- /tasks/subtasks - delete only Subtasks type

<details>
  <summary>Example 200 SUCCESS</summary>

```text
SUCCESS
```

</details>

- /tasks/[Task ID] - delete Task by ID, ID should be positive int

<details>
  <summary>Example 200 SUCCESS</summary>

```text
SUCCESS
```

</details>

<details>
  <summary>Example 404</summary>

```text
Error: Can't find Task 12
```

</details>