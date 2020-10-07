package data_structures.implementation;

import data_structures.Sorted;
import java.util.ArrayList;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {
    private Node head = new Node();

    public void add(T t) {
        Node pred = this.head;
        pred.lock();
        Node curr = pred.next;
        try {
            if(pred.data == null && pred.next == null){
                this.head = new Node(t);
            }
            else {
                assert pred.data != null;
                if (t.compareTo(pred.data) < 0) {
                    this.head = new Node(t, pred);
                }
                else {
                    while(pred.next != null) {
                        curr.lock();
                        try {
                            if (t.compareTo(curr.data) < 0) {
                                pred.next = new Node(t, curr);
                                return;
                            }
                        }
                        finally {
                            curr.unlock();
                        }
                    }
                    pred.next = new Node(t, null);
                }
            }
        } finally {
            pred.unlock();
        }
    }

    public synchronized void remove(T t) {
        this.head.lock();
        Node pred = this.head;
        Node curr = pred.next;

        try {
            if(pred.data != null && pred.next != null){
                this.head = new Node();
            }
            else {
                while(pred.next != null) {
                    curr.lock();

                    try {
                        if (t.compareTo(curr.data) == 0) {
                            pred.next = new Node();
                            return;
                        }
                    }
                    finally {
                        curr.unlock();
                    }
                }

            }
        } finally {
            pred.unlock();
        }
    }

    public synchronized ArrayList<T> toArrayList() {
        ArrayList<T> list = new ArrayList();
        if (this.head.next == null && this.head.data == null) {
            return list;
        }
        Node curr;
        for(curr = this.head; curr.next != null; curr = curr.next) {
            list.add(curr.data);
        }

        list.add(curr.data);

        return list;
    }

    private class Node {
        T data;
        Node next;
        Lock lock;

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
