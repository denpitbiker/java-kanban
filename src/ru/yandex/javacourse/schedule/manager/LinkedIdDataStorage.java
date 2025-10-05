package ru.yandex.javacourse.schedule.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class can store DATA linked by ID of this DATA, using combination of
 * HashMap and custom linked list implementation for O(1) remove(ID) operation support.
 */
class LinkedIdDataStorage<DATA, ID> {
    private final Map<ID, Node> _ids = new HashMap<>();
    private Node tail = null;
    private Node head = null;
    private int size = 0;

    /**
     * Add a new DATA to the storage with ID associated with data.
     * NOTE: id must be unique, otherwise DATA will replace existing data with the same ID.
     */
    void add(DATA data, ID dataId) {
        if (data == null || dataId == null) return;
        if (_ids.containsKey(dataId)) {
            removeNode(_ids.get(dataId));
        }
        _ids.put(dataId, addNode(data));
    }

    /**
     * Remove data associated with ID.
     */
    void remove(ID id) {
        if (id == null) return;
        if (_ids.containsKey(id)) {
            removeNode(_ids.remove(id));
        }
    }

    /**
     * Returns ArrayList representation of stored data.
     */
    ArrayList<DATA> getIndexedItems() {
        ArrayList<DATA> result = new ArrayList<>();
        Node tmp = tail;
        while (tmp != null) {
            result.add(tmp.data);
            tmp = tmp.next;
        }
        return result;
    }

    private Node addNode(DATA data) {
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
        DATA data;

        Node(Node prev, Node next, DATA data) {
            this.prev = prev;
            this.next = next;
            this.data = data;
        }
    }
}
