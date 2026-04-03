package manager;

import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager{

    private Map<Integer,Node> taskHistory;
    private Node head = null;
    private Node tail = null;

    public InMemoryHistoryManager() {
        taskHistory = new HashMap<>();
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        Node oldNode = taskHistory.get(task.getId());

        if (oldNode != null) {
            taskHistory.remove(task.getId());
            removeNode(oldNode);
        }

        Node newNode = linkLast(task);
        taskHistory.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node node = taskHistory.get(id);

        if (node == null) {
            return;
        }

        removeNode(node);
        taskHistory.remove(id);
    }

    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;

        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            tail = prevNode;
        }

        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            head = nextNode;
        }

        node.next = null;
        node.prev = null;
    }



    private Node linkLast(Task task) {
        Node node = new Node(tail, task);

        if (tail != null) {
            tail.next = node;
        } else {
            head = node;
        }

        tail = node;

        return  node;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node node = head;

        while(node != null) {
            tasks.add(node.task);
            node = node.next;
        }

        return tasks;
    }

    private static class Node {
        private Task task;
        private Node next;
        private Node prev;

        public Node(Node prev, Task task) {
            this.prev = prev;
            this.task = task;
            this.next = null;
        }

        public Task getTask() {
            return task;
        }
    }

}
