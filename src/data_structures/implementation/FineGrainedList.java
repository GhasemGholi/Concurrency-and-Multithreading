package data_structures.implementation;

import data_structures.Sorted;
import java.util.ArrayList;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {
    private volatile Node head = null;
    private final Lock reentrantLock = new ReentrantLock();

    public void add(T t) {

        // Condition: Add new node to empty list
        this.reentrantLock.lock();
        try {
            if (this.head == null) {
                this.head = new Node(t);
                return;
            }
        } finally {
            this.reentrantLock.unlock();
        }

        // Condition: Add node to a list with one element
        Node pred = this.head;
        pred.lock();
        try {
            if (pred.next == null) {
                Node newNode = new Node(t);
                if (t.compareTo(pred.data) > 0) {
                    pred.next = newNode;
                }
                else {
                    newNode.next = pred;
                    this.head = newNode;
                }
                return;
            }
        } finally {
            pred.unlock();
        }

        // Condition: Add node to a list with two or more elem
        pred = this.head;
        pred.lock();
        try {
            Node curr = pred.next;
            curr.lock();
            try {
                while (t.compareTo(curr.data) > 0) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (t.compareTo(curr.data) == 0) { // If element already containts, return
                    return;
                }

                Node newNode = new Node(t);
                if (curr.next == null) { // If the added element is bigger than all the elements in the list
                    curr.next = newNode; // Add to end of list
                }
                else {
                    newNode.next = curr; // Add inside the list.
                    pred.next = newNode;
                }
                // 1-> 2 -> 3
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
        this.reentrantLock.lock();
        try {
            if (this.head == null) { // If list empty, return
                return;
            }
        }
        finally {
            this.reentrantLock.unlock();
        }

        // Condition: There is one element in list
        Node pred = this.head;
        this.reentrantLock.lock();
        try {
            if (pred.next == null && t.compareTo(pred.data) == 0) {
                System.out.printf("NOT WORKINGGG\n pred: %s\n", pred.data);
                pred = pred.next;
                System.out.print(pred == null);
                return;
            }

        } finally {
            this.reentrantLock.unlock();
        }

        // Condition: There are two or more elements in list



        pred = this.head;
        Node curr = pred.next;
        pred.lock();
        try {
            curr = pred.next;
            curr.lock();

            try {
                while (t.compareTo(curr.data) > 0) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (t.compareTo(curr.data) == 0) {
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
        ArrayList<T> list = new ArrayList<>();
        if (this.head.next != null || this.head.data != null) {
            Node curr;
            for (curr = this.head; curr.next != null; curr = curr.next) {
                list.add(curr.data);
            }

            list.add(curr.data);
        }
        return list;
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

        public Node(T data, Node next) {
            this.data = data;
            this.next = next;
            this.lock = new ReentrantLock();
        }

        void lock() {
            this.lock.lock();
        }

        void unlock() {
            this.lock.unlock();
        }
    }
}
