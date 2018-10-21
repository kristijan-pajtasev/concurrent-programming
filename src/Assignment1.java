/* Assignment 1
 *
 * Student Name: Kristijan Pajtasev
 * Student Number: 2920266
 *
 */

import java.util.*;

public class Assignment1 {
    public static void main(String[] args) {
        //==================================================
        //Test code for Question 1
        System.out.println("Question 1");
        runAssignment1(80);
        System.out.println("");
        System.out.println("");

        //==================================================
        //Test code for Question2
        System.out.println("Question 2");
        runAssignment2(10000000);
    }

    static void runAssignment1(int diceThrows) {
        int[] diceResults = new int[diceThrows];
        Dice t = null;
        for(int i = 0; i < 4; i++) {
            t = new Dice(diceThrows / 4, diceResults, i * diceThrows / 4);
            t.run();
        }
        try {
            t.join();
            int[] results = new int[6];
            for(int diceThrow : diceResults) {
                results[diceThrow - 1]++;
            }
            for(int i = 0; i < 6; i ++) System.out.println("Number " + (i + 1) + " appears " + results[i] + " times");

        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    // task 2
    static void runAssignment2(int N) {
        int data[] = new int[N];
        //assume occurrence of zero equally likely for all numbers generated
        for(int j = 0; j < N; j++) data[j] = (int)(Math.random()*N);

        LeftmostZero t = null;
        int[] result = {-1, -1, -1, -1};
        for(int i = 0; i < 4; i++) {
            t = new LeftmostZero(i, data, N / 4, N / 4 * i, result);
            t.run();
        }
        try {
            t.join();
            for(int i = 0; i < 4; i++) {
                if(result[i] >= 0) {
                    System.out.println(result[i]);
                    return;
                }
            }
            System.out.println("No zero");
        } catch (InterruptedException e) { e.printStackTrace(); }

    }
}

//=========================================================
// Code for thread classes here

// Assignment 01 class
class Dice extends Thread {
    public Dice(int n, int[] results, int offset) {
        for(int i = 0; i < n; i++) {
            results[offset + i] = (int)Math.floor(Math.random() * 6) + 1;
        }
    }
}

// Assignment 02 class
class LeftmostZero extends Thread {
    public LeftmostZero(int index, int[] arr, int length, int offset, int[] result) {
        for(int i = 0; i < length; i++) {
            if(arr[offset + i] == 0) {
                result[index] = offset + i;
                break;
            }
        }
    }
}


