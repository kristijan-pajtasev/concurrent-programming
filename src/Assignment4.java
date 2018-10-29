import sun.nio.ch.ThreadPool;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

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
        final int NUM_OF_SEATS = 1; // TODO fix num of seats and num of customers
        final int NUM_OF_CUSTOMERS = 1;
        ViewingStand viewingStand = new ViewingStand(NUM_OF_SEATS);
        Customer[] customers = new Customer[NUM_OF_CUSTOMERS];
        for (int i = 0; i < NUM_OF_CUSTOMERS; i++) {
            Customer customer = new Customer(viewingStand, i);
            customers[i] = customer;
            customer.start();
        }

        // wait for all before proceeding to next questions
        for (int i = 0; i < NUM_OF_CUSTOMERS; i++) {
            try {
                customers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // TODO handle wait for all

        System.out.println("\n\n\n");

        ////////////////////////////////////////////
        // Question 2 test
        ////////////////////////////////////////////
        System.out.println("QUESTION 2 \n========================");

        CircularQueue queue = new CircularQueue<String>();
        queue.join("New string");
        queue.join("One more string");
        for(Object s: queue) {
            String s1 = (String) s;
            System.out.println(s1);
        }
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
// Question 2
////////////////////////////////////////////

class CircularQueue<T> implements Iterable<T> {
    private AtomicReferenceArray<T> queue;
    private AtomicInteger head, tail, size;
    private int max;

    public CircularQueue() {
        max = 20;
        queue = new AtomicReferenceArray(max);
        head = new AtomicInteger(0);
        tail = new AtomicInteger(0);
        size = new AtomicInteger(0);
    }

    public CircularQueue(int n) { //assume n >=0
        max = n;
        queue = new AtomicReferenceArray(max);
        head = new AtomicInteger(0);
        tail = new AtomicInteger(0);
        size = new AtomicInteger(0);
    }

    public boolean join(T x) {
        if (size.get() < max) {
            queue.set(tail.get(), x);
            tail.set((tail.get() + 1) % max);
            size.incrementAndGet();
            return true;
        } else return false;
    }

    public T top() {
        if (size.get() > 0)
            return queue.get(head.get());
        else
            return null;
    }

    public boolean leave() {
        if (size.get() == 0) return false;
        else {
            head.set((head.get() + 1) % max);
            size.decrementAndGet();
            return true;
        }
    }

    public boolean full() {
        return (size.get() == max);
    }

    public boolean empty() {
        return (size.get() == 0);
    }

    public Iterator<T> iterator() {
        return new QIterator<T>(queue, head, size, max);
    }

    private static class QIterator<T> implements Iterator<T> {
        private AtomicReferenceArray<T> d;
        private AtomicInteger index;
        private AtomicInteger size;
        private int max;
        private int returned = 0;

        QIterator(AtomicReferenceArray<T> dd, AtomicInteger head, AtomicInteger s, int m) {
            d = dd;
            index = head;
            size = s;
            max = m;
        }

        public boolean hasNext() {
            return returned < size.get();
        }

        public T next() {
            if (returned == size.get()) throw new NoSuchElementException();
            T item = d.get(index.get());
            index.set((index.get() + 1) % max);
            returned++;
            return item;
        }

        public void remove() {
        }
    }
}
