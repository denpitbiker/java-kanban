package ru.yandex.javacourse.schedule.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class can store Data linked by Id of this Data, using combination of
 * HashMap and custom linked list implementation for O(1) remove(Id) operation support.
 */
class LinkedIdDataStorage<Data, Id> {
    private final Map<Id, Node> ids = new HashMap<>();
    private Node tail = null;
    private Node head = null;
    private int size = 0;

    /**
     * Add a new Data to the storage with Id associated with data.
     * NOTE: id must be unique, otherwise Data will replace existing data with the same Id.
     */
    void add(Data data, Id dataId) {
        if (data == null || dataId == null) return;
        if (ids.containsKey(dataId)) {
            removeNode(ids.get(dataId));
        }
        ids.put(dataId, addNode(data));
    }

    /**
     * Remove data associated with Id.
     */
    void remove(Id id) {
        if (id == null) return;
        if (ids.containsKey(id)) {
            removeNode(ids.remove(id));
        }
    }

    /**
     * Returns ArrayList representation of stored data.
     */
    ArrayList<Data> getIndexedItems() {
        ArrayList<Data> result = new ArrayList<>();
        Node tmp = tail;
        while (tmp != null) {
            result.add(tmp.data);
            tmp = tmp.next;
        }
        return result;
    }

    private Node addNode(Data data) {
        Node newNode = new Node(null, null, data);
        if (size == 0) {
            tail = newNode;
        } else if (size == 1) {
            newNode.prev = tail;
            tail.next = newNode;
        } else {
            head.next = newNode;
            newNode.prev = head;
        }
        size++;
        head = newNode;
        return newNode;
    }

    private void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;
        if (size == 1) {
            tail = null;
            head = null;
        } else if (size == 2) {
            if (prev == null) {
                tail = head;
            } else {
                head = tail;
            }
        } else {
            if (prev != null) prev.next = next;
            if (next != null) next.prev = prev;
        }
        size--;
    }

    private class Node {
        Node prev;
        Node next;
        Data data;

        Node(Node prev, Node next, Data data) {
            this.prev = prev;
            this.next = next;
            this.data = data;
        }
    }
}
