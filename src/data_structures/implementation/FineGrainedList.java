package data_structures.implementation;

import data_structures.Sorted;
import java.util.ArrayList;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {
    private final Node head = new Node(); // OP constructor :D

    public void add(T t) {

        // Condition: Add new node to empty list
        this.head.lock();
        try {
            if (this.head.next == null) {
                this.head.next = new Node(t);
                return;
            }
        } finally {
            this.head.unlock();
        }

        Node pred = this.head;
        pred.lock();
        try {
            Node curr = pred.next;
            curr.lock();
            try {
                while (curr.next != null && t.compareTo(curr.data) > 0) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                Node newNode = new Node(t);
                // If the added element is bigger than all the elements in the list
                if (t.compareTo(curr.data) > 0) {
                    curr.next = newNode; // Add to end of list
                }

                // Add inside the list between nodes
                else {
                    pred.next = newNode;
                    newNode.next = curr;
                }

            } finally {
                curr.unlock();
            }
        }
        finally {
            pred.unlock();
        }
    }


    public void remove(T t) {

        // Condition: The list is empty
        this.head.lock();
        try {
            if (this.head.next == null) { // If list empty, return
                return;
            }
        }
        finally {
            this.head.unlock();
        }


        // Condition: There elements in list
        Node pred = this.head;
        pred.lock();
        try {
            Node curr = pred.next;
            curr.lock();
            try {
                while (curr.next != null && t.compareTo(curr.data) > 0) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (t.compareTo(curr.data) == 0) { // Remove element inside the list
                    pred.next = curr.next;
                }

            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }

    }

    public synchronized ArrayList<T> toArrayList() {
        ArrayList<T> arrayList = new ArrayList<>();

        if (this.head.next != null || this.head.data != null) {
            Node curr;
            for (curr = this.head; curr.next != null; curr = curr.next) {
                if (curr.data != null) {
                    arrayList.add(curr.data);
                }
            }

            if (curr.data != null) {
                arrayList.add(curr.data);
            }
        }

        // Empty list
        return arrayList;
    }

    private class Node {
        T data;
        Node next;
        Lock lock = new ReentrantLock();

        public Node() {
            this.data = null;
            this.next = null;
        }

        public Node(T data) {
            this.data = data;
            this.next = null;
        }

        void lock() {
            this.lock.lock();
        }

        void unlock() {
            this.lock.unlock();
        }
    }
}
