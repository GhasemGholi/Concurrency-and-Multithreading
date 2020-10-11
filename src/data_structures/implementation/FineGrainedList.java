package data_structures.implementation;

import data_structures.Sorted;
import java.util.ArrayList;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {
    private volatile Node head = null;
    private final Lock reentrantLock = new ReentrantLock();
    private ArrayList<T> arrayList;

    public void add(T t) {
        Node head;
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

        // Add to the beginning of list
        pred = this.head;
        pred.lock();
        try {
            if (t.compareTo(pred.data) < 0) { // Add to beginning of list if t is smaller than pred
                Node newNode = new Node(t);
                newNode.next = pred;
                this.head = newNode;
                return;
            }
        } finally {
            pred.unlock();
        }

        // Condition: Add node to a list with two or more elem
        // 2->3
        pred = this.head;
        pred.lock();
        try {
            Node curr = pred.next;
            curr.lock();
            try {
                while (curr.next != null && t.compareTo(curr.data) > 0) {
                    pred.unlock();
                    pred = curr;
                   // curr.unlock();
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

    public ArrayList<T> toArrayList() {
        this.arrayList = new ArrayList<>();
        if (this.head.next == null) {
            this.arrayList.add(this.head.data);
            return this.arrayList;
        }
        else if (this.head.next != null || this.head.data != null) {
            Node curr;
            for (curr = this.head; curr.next != null; curr = curr.next) {
                this.arrayList.add(curr.data);
            }

            this.arrayList.add(curr.data);
        }
        return this.arrayList;
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
            this.lock = new ReentrantLock();
        }
//
//        public Node(T data, Node next) {
//            this.data = data;
//            this.next = next;
//            this.lock = new ReentrantLock();
//        }

        void lock() {
            this.lock.lock();
        }

        void unlock() {
            this.lock.unlock();
        }
    }
}
