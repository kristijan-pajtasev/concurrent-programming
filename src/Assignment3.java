/**
 * Student Name: Kristijan Pajtasev
 * Student Number: 2920266
 */

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class Assignment3 {
    public static void main(String[] args) {
        // Test here
        //===============================================

        final int ARRAY_SIZE = 10000000;
        final int MAX_NUMBER = ARRAY_SIZE; // CHANGE THIS if max number diff from array size
        int[] data = new int[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) data[i] = (int) (Math.random() * MAX_NUMBER);

//        UNCOMMENT BELLOW TO PRINT INIT ARRAY
//        for(int i = 0; i < ARRAY_SIZE; i++) System.out.print(data[i] + " ");
//        System.out.println();

        long start = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(new MergeSort(data, 0, ARRAY_SIZE));
        long asynchRunningTime = System.currentTimeMillis() - start;

//      UNCOMMENT BELLOW TO PRINT SORTED ARRAY
//        for(int i = 0; i < ARRAY_SIZE; i++) System.out.print(data[i] + " ");
//        System.out.println();

        System.out.println("Fork pool execution time in milliseconds: " + asynchRunningTime);


        // generate new unsorted data array
        for (int i = 0; i < ARRAY_SIZE; i++) data[i] = (int) (Math.random() * 100);

//      UNCOMMENT BELLOW TO PRINT UNSORTED ARRAY
//        for(int i = 0; i < ARRAY_SIZE; i++) System.out.print(data[i] + " ");
//        System.out.println();

        start = System.currentTimeMillis();
        MergeSortSync.sort(data, 0, ARRAY_SIZE);
        long synchRunningTime = System.currentTimeMillis() - start;

//      UNCOMMENT BELLOW TO PRINT SORTED ARRAY
//        for(int i = 0; i < ARRAY_SIZE; i++) System.out.print(data[i] + " ");
//        System.out.println();

        System.out.println("Synchronous execution time in milliseconds: " + synchRunningTime);

        System.out.println("Asynchronous execution is " + (synchRunningTime - asynchRunningTime) +
                "ms faster than synchronous");
    }
}

/**
 * @class MergeSort
 * Class starting MergeSort as RecursiveAction. When array length reaches 100 or less switches to InsertionSort.
 * Otherwise splits in two and starts new MergeSort for each of two partition.
 */
class MergeSort extends RecursiveAction {
    private int data[];
    private int lowerBound;
    private int upperBound;

    MergeSort(int[] data, int lowerBound, int upperBound) {
        this.data = data;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    protected void compute() {
        int middle = (lowerBound + upperBound) / 2;
        if (upperBound - lowerBound < 100) {
            InsertionSort.sort(data, lowerBound, upperBound);
        } else {
            MergeSort sort1 = new MergeSort(data, lowerBound, middle);
            MergeSort sort2 = new MergeSort(data, middle, upperBound);
            invokeAll(sort1, sort2);
            sort1.join();
            sort2.join();
            MergeSortUtil.merge(data, lowerBound, middle, upperBound);
        }
    }
}

/**
 * @class MergeSortSync
 * Class starting merge sort. It starts splitting array util gets to size of 100 or less. Then switches to insertion
 * sort.
 */
class MergeSortSync {
    static void sort(int f[], int lb, int ub) {
        // switch to insertion sort when length less than 100
        if (ub - lb < 100) {
            InsertionSort.sort(f, lb, ub);
        } else {
            int mid = (lb + ub) / 2;
            MergeSortSync.sort(f, lb, mid);
            MergeSortSync.sort(f, mid, ub);
            MergeSortUtil.merge(f, lb, mid, ub);
        }
    }
}

/**
 * @class InsertionSort
 * Class containing sort method. It sorts part of array between lower and upper bound using insertion sort.
 */
class InsertionSort {
    static void sort(int data[], int lowerBound, int upperBound) {
        for (int i = lowerBound; i < upperBound; i++) {
            int j = i;
            while (j > lowerBound && data[j] < data[j - 1]) {
                int temp = data[j];
                data[j] = data[j - 1];
                data[j - 1] = temp;
                j--;
            }
        }
    }
}


/**
 * @class MergeSortUtil
 * Class with static method merge used for building back array after splitting
 */
class MergeSortUtil {
    static void merge(int f[], int lb, int mid, int ub) {
        int c[] = new int[ub - lb];
        int k = 0;
        int j = lb;
        int h = mid;
        while (j < mid && h < ub) {
            if (f[j] <= f[h]) {
                c[k] = f[j];
                j++;
            } else {
                c[k] = f[h];
                h++;
            }
            k++;
        }
        while (j < mid) {
            c[k] = f[j];
            k++;
            j++;
        }
        while (h < ub) {
            c[k] = f[h];
            k++;
            h++;
        }
        //Now copy data back to array
        for (int p = 0; p < c.length; p++)
            f[lb + p] = c[p];
    }
}
