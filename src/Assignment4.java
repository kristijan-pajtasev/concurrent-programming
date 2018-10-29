import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

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
        final int NUM_OF_CUSTOMERS = 30;
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

        System.out.println("\n\n\n");

        ////////////////////////////////////////////
        // Question 2 test
        ////////////////////////////////////////////
        System.out.println("QUESTION 2 \n========================");

        CircularQueue queue = new CircularQueue<String>();

        // intentionally more elements than queue size
        CircularQueueConsumer[] consumers = new CircularQueueConsumer[3];
        for (int i = 0; i < 3; i++) {
            CircularQueueConsumer consumer = new CircularQueueConsumer(i, queue);
            consumers[i] = consumer;
            consumer.start();
        }

        for (int i = 0; i < 3; i++) {
            try {
                consumers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n\nResulted array:");

        for (Object s : queue) {
            String s1 = (String) s;
            System.out.println(s1);
        }


        System.out.println("\n\n");

        // intentionally more elements than queue size
        CircularQueueRemoveConsumer[] removeConsumers = new CircularQueueRemoveConsumer[3];
        for (int i = 0; i < 3; i++) {
            CircularQueueRemoveConsumer consumer = new CircularQueueRemoveConsumer(i, queue);
            removeConsumers[i] = consumer;
            consumer.start();
        }

        for (int i = 0; i < 3; i++) {
            try {
                removeConsumers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n\nResulted array:");

        for (Object s : queue) {
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

class CircularQueueConsumer extends Thread {
    private CircularQueue<String> queue;
    private int index;

    CircularQueueConsumer(int index, CircularQueue<String> queue) {
        this.queue = queue;
        this.index = index;
    }

    public void run() {
        System.out.println("Circular customer " + index + " run");
        for (int i = 0; i < 10; i++) {
            String s = "Consumer " + index + " adding string " + i;
            queue.join(s);
        }
    }
}

class CircularQueueRemoveConsumer extends Thread {
    private CircularQueue<String> queue;
    private int index;

    CircularQueueRemoveConsumer(int index, CircularQueue<String> queue) {
        this.queue = queue;
        this.index = index;
    }

    public void run() {
        System.out.println("Circular remove customer " + index + " run");
        for (int i = 0; i < 10; i++) {
            queue.leave();
        }
    }
}

class CircularQueue<T> implements Iterable<T> {
    private T queue[];
    private AtomicInteger head, tail, size;
    private int max;

    CircularQueue() {
        max = 20;
        queue = (T[]) new Object[max];
        head = new AtomicInteger(0);
        tail = new AtomicInteger(0);
        size = new AtomicInteger(0);
    }

    public CircularQueue(int n) { //assume n >=0
        max = n;
        queue = (T[]) new Object[max];
        head = new AtomicInteger(0);
        tail = new AtomicInteger(0);
        size = new AtomicInteger(0);
    }

    synchronized boolean join(T x) {
        if (!full()) {
            setAndIncreaseCounters(x);
            return true;
        } else return false;
    }

    public T top() {
        if (size.get() > 0)
            return queue[head.get()];
        else
            return null;
    }

    synchronized boolean leave() {
        if (empty()) return false;
        else {
            setAndDecreaseCounters();
            return true;
        }
    }

    private void setAndIncreaseCounters(T x) {
        System.out.println(x.toString());
        queue[tail.get()] = x;
        tail.set((tail.get() + 1) % max);
        size.incrementAndGet();
    }

    private void setAndDecreaseCounters() {
        System.out.println("Item removed from queue");
        head.set((head.get() + 1) % max);
        size.decrementAndGet();
    }

    boolean full() {
        return (size.get() == max);
    }

    boolean empty() {
        return (size.get() == 0);
    }

    public Iterator<T> iterator() {
        return new QIterator<T>(queue, head, size, max);
    }

    private static class QIterator<T> implements Iterator<T> {
        private T[] d;
        private AtomicInteger index;
        private AtomicInteger size;
        private int max;
        private int returned = 0;

        QIterator(T[] dd, AtomicInteger head, AtomicInteger s, int m) {
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
            T item = d[index.get()];
            index.set((index.get() + 1) % max);
            returned++;
            return item;
        }

        public void remove() {
        }
    }
}
