package data_structures.implementation;

import data_structures.Sorted;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainedTree<T extends Comparable<T>> implements Sorted<T> {
    // Init 'node', 'tree', and 'lock'
    private volatile Node node;
    private ArrayList<T> tree;
    private final Lock lock = new ReentrantLock();

    public CoarseGrainedTree() {
        this.node = null;
    }

    public synchronized void add(T t) {
        this.lock.lock();
        node = addToTree(t, node);
        this.lock.unlock();
    }

    public synchronized void remove(T t) {
        this.lock.lock();
        node = removeNode(t, node);
        this.lock.unlock();
    }

    public ArrayList<T> toArrayList() {
        this.tree = new ArrayList<>();
        this.traverse(node);
        return this.tree;
    }

    // TODO: Implement lock algorithm to prevent deadlock and starvation
    public void lockAlgorithm() {}

    // HELPER FUNCTIONS
    public Node addToTree(T t, Node element) {
        if(element == null) return new Node(t);
        if (t.compareTo(element.data) < 0) {
            element.left = addToTree(t, element.left);
        }
        else {
            element.right = addToTree(t, element.right);
        }
        return element;
    }

    public Node removeSmallest(Node element) {
        if(element.left == null) return element;
        else return removeSmallest(element.left);
    }

    public Node removeNode(T t, Node element) {
        if(element == null) return null;
        else if (t.compareTo(element.data) < 0) {
            element.left = removeNode(t, element.left);
        }
        else if (t.compareTo(element.data) > 0) {
            element.right = removeNode(t, element.right);
        }
        else if (element.left == null) {
            element = element.right;
        }
        else if (element.right == null) {
            element = element.left;
        }
        else {
            element = removeSmallest(element.left);
            element.right = removeNode(element.data, element.right);
        }
        return element;
    }

    private void traverse(Node element) {
        if (element != null){
            this.traverse(element.left);
            this.tree.add(element.data);
            this.traverse(element.right);
        }
    }

    // NODE
    private class Node {
        private final T data;
        private CoarseGrainedTree<T>.Node left;
        private CoarseGrainedTree<T>.Node right;

        public Node(T data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }
    }
}
