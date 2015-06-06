package destructo.datastructures;

import java.util.LinkedList;

public class CircularQueue<T> extends LinkedList {

    private int maxLength;
    public CircularQueue(int maxLength) {
        super();
        this.maxLength = maxLength;
    }

    @Override
    public boolean add(Object e) {
        super.addFirst(e);
        while(super.size() > maxLength) {
            super.removeLast();
        }
        return true;
    }
}