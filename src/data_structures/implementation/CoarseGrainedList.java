package data_structures.implementation;

import data_structures.Sorted;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainedList<T extends Comparable<T>> implements Sorted<T> {

    // Init 'head' and 'lock'
    volatile Node head; // All the threads can see the changes to the list.
    Lock lock = new ReentrantLock();

    // TODO: Implement lock algorithm to prevent deadlock and
    private void lockAlgorithm() {}


    public CoarseGrainedList() {
        head = new Node();
    }

    public synchronized void add(T t) {
        this.lock.lock();
        if (this.head.next == null && this.head.data == null) {
            this.head = new Node(t);
        }
        else {
            Node Head = this.head;
            assert Head.data != null;
            if (t.compareTo(Head.data) <= 0) {
                Head = new Node(t, this.head);
                this.head = Head;
            }
            else {
                Node prev;
                for(prev = Head; t.compareTo(Head.data != null ? Head.data : null) > 0; Head = Head.next) {
                    if (Head.next == null) {
                        Head.next = new Node(t, null);
                        this.lock.unlock();
                        return;
                    }
                    prev = Head;
                }
                prev.next = new Node(t, Head);
            }
        }
        this.lock.unlock();
    }

    public synchronized void remove(T t) {
        this.lock.lock();
        if(this.head.next == null){
            this.head = new Node();
            lock.unlock();
            return;
        }
        else if (this.head.data != null) {
            Node Head = this.head;
            assert Head.data != null;
            if (t.compareTo(Head.data) == 0) {
                this.head = this.head.next;
            } else {
                Node prev;
                for (prev = Head; t.compareTo(Head.data != null ? Head.data : null) != 0; Head = Head.next) {
                    prev = Head;
                    if (Head.next == null) {
                        this.lock.unlock();
                        return;
                    }
                }
                prev.next = Head.next;
            }
        }
        this.lock.unlock();
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
        private CoarseGrainedList<T>.Node next;
        private final T data;

        // multiple constructors

        public Node() {
            this.next = null;
            this.data = null;
        }

        public Node(T data) {
            this.data = data;
            this.next = null;
        }

        public Node(T data, CoarseGrainedList<T>.Node next) {
            this.data = data;
            this.next = next;
        }
    }
}
