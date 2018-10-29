import com.sun.glass.ui.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Assignment4.java
 * Student Name: Kristijan Pajtasev
 * Student Number: 2920266
 */

public class Assignment4 {
    public static void main(String args[]) {
        ////////////////////////////////////////////
        // Question 1 test
        ////////////////////////////////////////////
        System.out.println("QUESTION 1 \n========================");
        final int NUM_OF_SEATS = 5;
        ViewingStand viewingStand = new ViewingStand(NUM_OF_SEATS);

        for (int i = 0; i < 30; i++) {
            Customer customer = new Customer(viewingStand, i);
            customer.start();
        }

        System.out.println("\n\n\n");

        ////////////////////////////////////////////
        // Question 2 test
        ////////////////////////////////////////////
        System.out.println("QUESTION 1 \n ========================");
    }
}


class Customer extends Thread {
    private ViewingStand viewingStand;
    private int customerId;

    Customer(ViewingStand viewingStand, int customerId) {
        this.viewingStand = viewingStand;
        this.customerId = customerId;
    }

    public void run() {
        System.out.println("Customer " + customerId + " requesting seat.");
        int seatNumber;

        do {
            seatNumber = viewingStand.findSeat();

            if (seatNumber > -1) {
                int time = (int) (Math.random() * 10);
                System.out.println("Customer " + customerId + " taking seat " + seatNumber + " for " + time + "s");

                try {
                    Thread.sleep(time * 1000);
                    viewingStand.leaveSeat(seatNumber);
                    System.out.println("Customer " + customerId + " leaving seat ");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while (seatNumber == -1);
    }

}

////////////////////////////////////////////
// Question 1
////////////////////////////////////////////
class ViewingStand {
    private int length;
    private AtomicIntegerArray seats;

    ViewingStand(int length) {
        this.length = length;
        seats = new AtomicIntegerArray(length);
        for (int i = 0; i < length; i++) seats.set(i, 0);
    }

    int findSeat() {
        for (int i = 0; i < length; i++) {
            boolean availableSeat = seats.compareAndSet(i, 0, 1);
            if (availableSeat) {
                return i;
            }
        }
        return -1;
    }

    void leaveSeat(int seat) {
        seats.set(seat, 0);
    }
}

////////////////////////////////////////////
// Question 1 test
////////////////////////////////////////////
class CircularQueue<T> implements Iterable<T> {
    private T queue[];
    private int head, tail, size;

    public CircularQueue() {
        queue = (T[]) new Object[20];
        head = 0;
        tail = 0;
        size = 0;
    }

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
