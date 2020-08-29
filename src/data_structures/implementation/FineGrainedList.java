package data_structures.implementation;

import java.util.ArrayList;

import data_structures.Sorted;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {

    public void add(T t) {
        throw new UnsupportedOperationException();
    }

    public void remove(T t) {
        throw new UnsupportedOperationException();
    }

    public ArrayList<T> toArrayList() {
        throw new UnsupportedOperationException();
    }
}
