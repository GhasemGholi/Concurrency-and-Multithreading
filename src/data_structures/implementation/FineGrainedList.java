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
        Node pred = this.head;
        try {
            if (pred == null) {
                pred = new Node(t);
                this.head = pred;
                return;
            }
        } finally {
            this.reentrantLock.unlock();
        }

        // Condition: Add node to a list with one element
        pred = this.head;
        pred.lock();
        try {
            if (pred.next == null) {
                Node newNode = new Node(t);
                if (t.compareTo(pred.data) > 0) {
                    pred.next = newNode;
                }
                else {
                    this.head = newNode;
                    newNode.next = pred;
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
            if (t.compareTo(pred.data) < 0 && pred.next != null) { // Add to beginning of list if t is smaller than pred
                Node newNode = new Node(t);
                this.head = newNode;
                newNode.next = pred.next;
                return;
            }
        } finally {
            pred.unlock();
        }

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
                Node newNode = new Node(t);

                if (t.compareTo(curr.data) != 0) { // If element already containts, return
                    newNode.next = curr; // Add inside the list.
                    pred.next = newNode;
                }

                else if (curr.next == null) { // If the added element is bigger than all the elements in the list
                    curr = newNode; // Add to end of list
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
            if (pred.next == null) {
                pred = new Node();
                this.head = pred;
                return;
            }

        } finally {
            this.reentrantLock.unlock();
        }


        // Remove element from beginning of list
        pred = this.head;
        pred.lock();
        try {
            if (t.compareTo(pred.data) == 0 && pred.next != null) {
                this.head = pred.next;
                return;
            }
        } finally {
            pred.unlock();
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

    public ArrayList<T> toArrayList() {
        ArrayList<T> arrayList = new ArrayList<>();
        if (this.head.next == null && this.head.data != null) {
            arrayList.add(this.head.data);
            return arrayList;
        }
        else if (this.head.next != null || this.head.data != null) {
            Node curr;
            for (curr = this.head; curr.next != null; curr = curr.next) {
                if (curr.data != null) {
                    arrayList.add(curr.data);
                }
            }

            arrayList.add(curr.data);
        }
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
            this.lock = new ReentrantLock();
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
