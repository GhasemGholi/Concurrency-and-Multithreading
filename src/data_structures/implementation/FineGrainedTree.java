package data_structures.implementation;

import data_structures.Sorted;
import java.util.ArrayList;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedTree<T extends Comparable<T>> implements Sorted<T> {
    private bstNode root = null;
    private ArrayList<T> arrayList;
    private final Lock bstLock = new ReentrantLock();

    public void add(T data) {
        bstNode currentLeaf;
        bstNode currentNode = null;
        this.bstLock.lock();

        try {
            if (this.root == null) {
                this.root = new bstNode(data);
                return;
            }
        }
        finally {
            this.bstLock.unlock();
        }

        currentLeaf = this.root;
        currentLeaf.lock();

        try {
            while (currentLeaf != null) {
                currentNode = currentLeaf;
                if (currentLeaf.data.compareTo(data) > 0) {
                    currentLeaf = currentLeaf.left;
                }
                else {
                    currentLeaf = currentLeaf.right;
                }

                if (currentLeaf != null) {
                    currentLeaf.lock();
                    currentNode.unlock();
                }
            }

            if (currentNode.data.compareTo(data) > 0) {
                currentNode.left = new bstNode(data);
            }
            else {
                currentNode.right = new bstNode(data);
            }
        } finally {
            if (currentLeaf != currentNode && currentLeaf != null) {
                currentLeaf.unlock();
            }
            currentNode.unlock();

        }

    }

    private bstNode reorder(bstNode child) {
        bstNode currentNode = child;
        bstNode currentLeaf;
        if (child.left != null) {
            currentLeaf = child.left;
            currentLeaf.lock();

            try {
                while (currentLeaf.right != null) {
                    if (currentNode != child) {
                        currentNode.unlock();
                    }
                    currentNode = currentLeaf;
                    currentLeaf = currentLeaf.right;
                    currentLeaf.lock();
                }

                if (currentLeaf.left != null) {
                    currentLeaf.left.lock();
                }

                try {
                    if (currentNode == child) {
                        currentNode.left = currentLeaf.left;
                    }
                    else {
                        currentNode.right = currentLeaf.left;
                    }
                }
                finally {
                    if (currentLeaf.left != null) {
                        currentLeaf.left.unlock();
                    }
                }
            }
            finally {
                if (currentNode != child && currentNode != currentLeaf) {
                    currentNode.unlock();
                }
                currentLeaf.unlock();
            }
        }
        else {
            if (child.right == null) {
                return null;
            }

            currentLeaf = child.right;
            currentLeaf.lock();

            try {
                if (currentLeaf.left != null) {
                    do {
                        if (currentNode != child) {
                            currentNode.unlock();
                        }

                        currentNode = currentLeaf;
                        currentLeaf = currentLeaf.left;
                        currentLeaf.lock();
                    }while (currentLeaf.left != null);
                }

                if (currentLeaf.right != null) {
                    currentLeaf.right.lock();
                }

                try {
                    if (currentNode == child) {
                        currentNode.right = currentLeaf.right;
                    }
                    else {
                        currentNode.left = currentLeaf.right;
                    }
                }
                finally {
                    if (currentLeaf.right != null) {
                        currentLeaf.right.unlock();
                    }
                }
            }
            finally {
                if (currentNode != child && currentNode != currentLeaf) {
                    currentNode.unlock();
                }
                currentLeaf.unlock();
            }
        }
        return currentLeaf;
    }

    public void remove(T data) {
        this.bstLock.lock();

        bstNode currentLeaf;
        bstNode currentNode;
        bstNode cutLeaf;
        try {
            assert this.root != null;
            currentNode = currentLeaf = this.root;
            currentLeaf.lock();

            try {
                if (currentLeaf.data.compareTo(data) > 0) {
                    currentLeaf = currentLeaf.left;
                }
                else {
                    if (currentLeaf.data.compareTo(data) >= 0) {
                        cutLeaf = this.reorder(currentLeaf);
                        this.root = cutLeaf;
                        if (cutLeaf != null) {
                            cutLeaf.left = currentLeaf.left;
                            cutLeaf.right = currentLeaf.right;
                        }
                        return;
                    }
                    currentLeaf = currentLeaf.right;
                }
            }
            finally {
                if (currentLeaf != currentNode.left && currentLeaf != currentNode.right) {
                    currentLeaf.unlock();
                }
            }
        }
        finally {
            this.bstLock.unlock();
        }

        currentLeaf.lock();

        try {
            while (currentLeaf != null) {
                if (currentLeaf.data.compareTo(data) == 0) {
                    cutLeaf = this.reorder(currentLeaf);
                    if (currentNode.data.compareTo(data) > 0) {
                        currentNode.left = cutLeaf;
                    }
                    else {
                        currentNode.right = cutLeaf;
                    }
                    if (cutLeaf != null) {
                        cutLeaf.left = currentLeaf.left;
                        cutLeaf.right = currentLeaf.right;
                    }
                    return;
                }

                currentNode.unlock();
                currentNode = currentLeaf;
                if (currentLeaf.data.compareTo(data) > 0) {
                    currentLeaf = currentLeaf.left;
                }
                else {
                    currentLeaf = currentLeaf.right;
                }
                if (currentLeaf != null) {
                    currentLeaf.lock();
                }
            }
        }
        finally {
            if (currentLeaf != currentNode) {
                assert currentLeaf != null;
                currentLeaf.unlock();
                currentNode.unlock();
            }
            else {
                currentLeaf.unlock();
            }
        }
    }

    private void traverseTree(bstNode root) {
        if (root != null) {
            this.traverseTree(root.left);
            this.arrayList.add(root.data);
            this.traverseTree(root.right);
        }
    }

    public ArrayList<T> toArrayList() {
        this.arrayList = new ArrayList();
        this.traverseTree(this.root);
        return this.arrayList;
    }

    private class bstNode {
        T data;
        bstNode left;
        bstNode right;
        Lock lock;

        public bstNode(T data) {
            this(data, null, null);
        }

        public bstNode(T data, bstNode left, bstNode right) {
            this.data = data;
            this.left = left;
            this.right = right;
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
