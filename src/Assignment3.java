/**
 * Student Name:
 * Student Number:
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
        for(int i = 0; i < ARRAY_SIZE; i++) System.out.print(data[i] + " ");
        System.out.println();

        long start = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(new Sort(data, 0, ARRAY_SIZE));
        long end = System.currentTimeMillis();

//      UNCOMMENT BELLOW TO PRINT SORTED ARRAY
        for(int i = 0; i < ARRAY_SIZE; i++) System.out.print(data[i] + " ");
        System.out.println();

        System.out.println("Fork pool execution time in milliseconds: " + (end - start));


        // generate new unsorted data array
        for (int i = 0; i < ARRAY_SIZE; i++) data[i] = (int) (Math.random() * 100);

//      UNCOMMENT BELLOW TO PRINT UNSORTED ARRAY
        for(int i = 0; i < ARRAY_SIZE; i++) System.out.print(data[i] + " ");
        System.out.println();

        start = System.currentTimeMillis();
        mergeSort(data, 0, ARRAY_SIZE);
        end = System.currentTimeMillis();

//      UNCOMMENT BELLOW TO PRINT SORTED ARRAY
        for(int i = 0; i < ARRAY_SIZE; i++) System.out.print(data[i] + " ");
        System.out.println();

        System.out.println("Synchronous execution time in milliseconds: " + (end - start));
    }

    static void mergeSort(int f[], int lb, int ub) {
        // switch to insertion sort when length less than 100
        if (ub - lb < 100) {
            InsertionSort.sort(f, lb, ub);
        } else {
            int mid = (lb + ub) / 2;
            mergeSort(f, lb, mid);
            mergeSort(f, mid, ub);
            MergeSortUtil.merge(f, lb, mid, ub);
        }
    }

}

class Sort extends RecursiveAction {
    private int data[];
    private int lowerBound;
    private int upperBound;

    Sort(int[] data, int lowerBound, int upperBound) {
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
            Sort sort1 = new Sort(data, lowerBound, middle);
            Sort sort2 = new Sort(data, middle, upperBound);
            invokeAll(sort1, sort2);
            sort1.join();
            sort2.join();
            MergeSortUtil.merge(data, lowerBound, middle, upperBound);
        }
    }

}

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
