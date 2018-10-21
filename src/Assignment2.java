import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Student Name: Kristijan Pajtasev
 * Student number: 2920266
 */

public class Assignment2 {
    public static void main(String[] args) {
        //Question 1
        executeFirstQuestion();

        //========================================
        //Question 2
        executeSecondQuestion();

        //=======================================
    }

    // question 1
    public static void executeFirstQuestion() {
        int ARRAY_SIZE = 1000001;
        int THREADS_COUNT = 4;
        int arr[] = new int[ARRAY_SIZE];

        // generate data array
        for (int j = 0; j < arr.length; j++) arr[j] = (int) (Math.random() * 100000);

        // generate data array determine bucket size
        int bucketSize = ARRAY_SIZE / THREADS_COUNT;
        int overflow = ARRAY_SIZE % THREADS_COUNT;

        // create pool
        ExecutorService pool = Executors.newFixedThreadPool(THREADS_COUNT);
        ArrayList<Future> future = new ArrayList<>();

        // initialize jobs, add +1 to length for fixing bucket sizes
        for (int i = 0; i < THREADS_COUNT; i++) {
            int currentBucketSize = i < overflow ? bucketSize + 1 : bucketSize;
            int startIndex = i < overflow ? i * (bucketSize + 1) : (i * bucketSize + overflow);
            Future<Integer> f = pool.submit(new EvenFrequency(arr, startIndex, currentBucketSize));
            future.add(f);
        }

        int totalFreq = 0;


        // sum all frequencies
        for (Future<Integer> f : future) {
            try {
                totalFreq += f.get().intValue();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }

        pool.shutdown();
        System.out.println("Total number of even numbers: " + totalFreq);
    }

    // question 2
    public static void executeSecondQuestion() {
        int ARRAY_SIZE = 1000000;
        int arr[] = new int[ARRAY_SIZE];
        // generate array
        for (int j = 0; j < arr.length; j++) arr[j] = (int) (Math.random() * 100000);

        int numberOfProcessors = Runtime.getRuntime().availableProcessors();

        // create pool, determine partition size
        ExecutorService pool2 = Executors.newFixedThreadPool(numberOfProcessors);
        Future[] future = new Future[numberOfProcessors];
        int bucketSize = arr.length / numberOfProcessors;

        // add all jobs, handle last partition size
        for (int i = 0; i < numberOfProcessors; i++) {
            int partitionLength = i + 1 == numberOfProcessors ? bucketSize + ARRAY_SIZE % numberOfProcessors : bucketSize;
            Future<Integer[]> f = pool2.submit(new MaxNumberFrequency(arr, i * bucketSize, partitionLength));
            future[i] = f;
        }

        try {
            // initial max as first result
            Integer[] firstResult = (Integer[]) future[0].get();
            int max = firstResult[0];
            int count = firstResult[1];

            // if matched greater, set it as max, if equal add to count
            for (int i = 1; i < numberOfProcessors; i++) {
                Integer[] result = (Integer[]) future[i].get();
                if (result[0] > max) {
                    max = result[0];
                    count = result[1];
                } else if (max == result[0]) count += result[1];
            }

            System.out.println("Max number is: " + max + " and it is appearing " + count + " times.");
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
            System.out.println(e);
        }

        pool2.shutdown();
    }
}
//Code for threads for Question 1=========================

class EvenFrequency implements Callable<Integer> {
    private int[] arr;
    private int startIndex, length;

    public EvenFrequency(int[] arr, int startIndex, int length) {
        this.arr = arr;
        this.startIndex = startIndex;
        this.length = length;
    }

    // start from given index and count evens for given length of partition
    @Override
    public Integer call() {
        int freq = 0;
        for (int i = 0; i < length; i++) {
            if (arr[startIndex + i] % 2 == 0) freq++;
        }
        return freq;
    }
}


//=======================================================
//Code for Callable class here
class MaxNumberFrequency implements Callable<Integer[]> {
    int[] arr;
    int startIndex, length;

    public MaxNumberFrequency(int[] arr, int startIndex, int length) {
        this.arr = arr;
        this.startIndex = startIndex;
        this.length = length;
    }

    // start from given index as current max, if found new greater use it as new max, if equal add +1 to count
    @Override
    public Integer[] call() {
        int max = arr[startIndex], count = 1;
        for (int i = 1; i < length; i++) {
            if (arr[i + startIndex] == max) count++;
            else if (arr[i + startIndex] > max) {
                max = arr[i + startIndex];
                count = 1;
            }
        }

        Integer[] result = {max, count};
        return result;
    }
}
//========================================================