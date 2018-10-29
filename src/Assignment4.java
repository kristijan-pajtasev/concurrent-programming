import com.sun.glass.ui.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Assignment4.java
 * <p>
 * Student Name: Kristijan Pajtasev
 * Student Number: 2920266
 */

public class Assignment4 {
    public static void main(String args[]) {
        final int NUM_OF_SEATS = 5;
        ViewingStand viewingStand = new ViewingStand(NUM_OF_SEATS);

        for (int i = 0; i < 10; i++) {
            Customer customer = new Customer(viewingStand, i);
            customer.start();
        }
    }
}


class Customer extends Thread {
    ViewingStand viewingStand;
    int seatNumber, customerId;

    public Customer(ViewingStand viewingStand, int customerId) {
        this.viewingStand = viewingStand;
        this.customerId = customerId;
    }

    public void run() {
        do {
            seatNumber = viewingStand.findSeat();
            System.out.println("Customer " + customerId + " requesting seat");

            if (seatNumber > -1) {
                int time = (int) (Math.random() * 10) * 1000;
                System.out.println("Customer " + customerId + " requesting seat " +
                        seatNumber + " for " + time + "ms");
                try {
                    Thread.sleep(time); // TODO, why not sleep
                    viewingStand.leaveSeat(seatNumber);
                    System.out.println("Customer " + customerId + " releasing seat ");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while (seatNumber == -1);
    }

}

class ViewingStand {
    private Semaphore stand;
    ArrayList<Boolean> seats;
    AtomicIntegerArray arr;

    public ViewingStand(int max) {
        stand = new Semaphore(max);
        seats = new ArrayList<>();
        arr = new AtomicIntegerArray(max);
        for (int i = 0; i < max; i++) seats.add(false);
    }

    public int findSeat() {
        int freeSeat = seats.indexOf(false);
        try {
            if (freeSeat > 0) {
                seats.add(freeSeat, true);
                stand.acquire();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            return freeSeat;
        }
    }

    private synchronized int freeSeat() {
        return 0;
    }

    public void leaveSeat(int seat) {
        seats.set(seat, false);
    }
}

class CircularQueue<T> implements Iterable<T> {
    private T queue[];
    private int head, tail, size;

    @SuppressWarnings("unchecked")
    public CircularQueue() {
        queue = (T[]) new Object[20];
        head = 0;
        tail = 0;
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public CircularQueue(int n) { //assume n >=0
        queue = (T[]) new Object[n];
        size = 0;
        head = 0;
        tail = 0;
    }

    public boolean join(T x) {
        if (size < queue.length) {
            queue[tail] = x;
            tail = (tail + 1) % queue.length;
            size++;
            return true;
        } else return false;
    }

    public T top() {
        if (size > 0)
            return queue[head];
        else
            return null;
    }

    public boolean leave() {
        if (size == 0) return false;
        else {
            head = (head + 1) % queue.length;
            size--;
            return true;
        }
    }

    public boolean full() {
        return (size == queue.length);
    }

    public boolean empty() {
        return (size == 0);
    }

    public Iterator<T> iterator() {
        return new QIterator<T>(queue, head, size);
    }

    private static class QIterator<T> implements Iterator<T> {
        private T[] d;
        private int index;
        private int size;
        private int returned = 0;

        QIterator(T[] dd, int head, int s) {
            d = dd;
            index = head;
            size = s;
        }

        public boolean hasNext() {
            return returned < size;
        }

        public T next() {
            if (returned == size) throw new NoSuchElementException();
            T item = (T) d[index];
            index = (index + 1) % d.length;
            returned++;
            return item;
        }

        public void remove() {
        }
    }
}
