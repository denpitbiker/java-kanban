package ru.yandex.javacourse.schedule.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class can store Data linked by Id of this Data, using combination of
 * HashMap and custom linked list implementation for O(1) remove(Id) operation support.
 */
class LinkedIdDataStorage<D, I> {
    private final Map<I, Node> ids = new HashMap<>();
    private Node tail = null;
    private Node head = null;

    /**
     * Add a new Data to the storage with Id associated with data.
     * NOTE: id must be unique, otherwise Data will replace existing data with the same Id.
     */
    void add(D data, I dataId) {
        if (data == null || dataId == null) return;
        if (ids.containsKey(dataId)) {
            removeNode(ids.get(dataId));
        }
        ids.put(dataId, addNode(data));
    }

    /**
     * Remove data associated with Id.
     */
    void remove(I id) {
        if (id == null) return;
        if (ids.containsKey(id)) {
            removeNode(ids.remove(id));
        }
    }

    /**
     * Returns ArrayList representation of stored data.
     */
    ArrayList<D> getIndexedItems() {
        ArrayList<D> result = new ArrayList<>();
        Node tmp = tail;
        while (tmp != null) {
            result.add(tmp.data);
            tmp = tmp.next;
        }
        return result;
    }

    private Node addNode(D data) {
        Node newNode = new Node(head, null, data);
        if (head != null) {
            head.next = newNode;
        }
        head = newNode;
        if (tail == null) {
            tail = newNode;
        }
        return newNode;
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            tail = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            head = node.prev;
        }
    }

    private class Node {
        Node prev;
        Node next;
        D data;

        Node(Node prev, Node next, D data) {
            this.prev = prev;
            this.next = next;
            this.data = data;
        }
    }
}
