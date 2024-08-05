package taskmanager.service.implementation;

import taskmanager.model.Task;

import java.text.SimpleDateFormat;
import java.util.*;


public class HistoryLog {
    private Map<Integer, Node> log = new HashMap<>();

    class Node {
        public Task task;
        public Node next;
        public Node prev;
        public Date date;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(task, node.task) && Objects.equals(next, node.next) && Objects.equals(prev, node.prev);
        }

        @Override
        public int hashCode() {
            return Objects.hash(task, next, prev);
        }

        public Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
            this.date = new Date();
        }
    }

    private Node head;

    private Node tail;

    private int size = 0;

    public void add(Task task) {
        int id = task.getId();

        if (log.containsKey(id)) {
            remove(id);
        }

        if (log.isEmpty()) {
            Node node = new Node(null, task, null);
            head = tail = node;
            log.put(id, node);
        } else {
            Node node = new Node(tail, task, null);
            tail.next = node;
            tail = node;
            log.put(id, node);
        }

        size++;
    }

    public void remove(int id) {
        if (log.isEmpty() || !log.containsKey(id)) return;

        if (log.size() == 1) {
            log.remove(id);

            head = null;
            tail = null;
            size = 0;
        } else {
            Node node = log.get(id);

            if (node.equals(head)) {
                head = node.next;
                node.next.prev = null;
            } else if (node.equals(tail)) {
                tail = node.prev;
                node.prev.next = null;
            } else {
                node.next.prev = node.prev;
                node.prev.next = node.next;
            }

            log.remove(id);
            size--;
        }
    }

    public List<String> getLog() {
        List<String> result = new ArrayList<>();

        SimpleDateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        Node node = head;
        while (node != null) {
            String historyData = "ID:" + node.task.getId() + " " +
                    "Type:" + node.task.getClass().getSimpleName() + " " +
                    "Name:" + node.task.getName() + " - " +
                    dateformat.format(node.date);

            result.add(historyData);
            node = node.next;
        }

        return result;
    }

    public int size() {
        return size;
    }
}
