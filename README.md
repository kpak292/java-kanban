# java-kanban
Repository for homework project.
## Endpoints

**/tasks** 
Endpoint for work with Taskmanager

**GET**
- /tasks/all - retrieve all tasks as json

<example>
  <title>Example</title>

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

</example>
