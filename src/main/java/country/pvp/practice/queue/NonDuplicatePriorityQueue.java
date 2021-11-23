package country.pvp.practice.queue;

import java.util.PriorityQueue;

public class NonDuplicatePriorityQueue<E> extends PriorityQueue<E> {

    @Override
    public boolean add(E e) {
        if (contains(e)) return false;

        return super.add(e);
    }
}
