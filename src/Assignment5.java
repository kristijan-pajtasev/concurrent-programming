import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Assignment5.java
 * Student Name: Kristijan Pajtasev
 * Student Number: 2920266
 */

public class Assignment5 {
    public static void main(String args[]) {
        //Question 1
        //===============================================
        questionOneTest();


        //Question 2
        //==============================================


        //===============================================

        //Question 3
        //==============================================


        //===============================================
    }

    static void questionOneTest() {
        final int NUMBER_OF_THREADS = 5;
        CollectionPoint points = new CollectionPoint();
        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++) {
            Thread thread = new AddPoints(points, i);
            threads[i] = thread;
            thread.start();
        }


        try {
            for (int i = 0; i < NUMBER_OF_THREADS; i++) {
                threads[i].join();
            }
            System.out.println("================================= \nQuestion 1: ");
            System.out.println(points.toString());
            System.out.println("Number of points generated: " + points.size());

            System.out.println("Number of points with x: " + points.getAllX(2).size());

            System.out.println("Collection contains (-1, -1): " + points.search(new Point(-1, -1)));
            System.out.println("Add (-1, -1): ");
            points.add(new Point(-1, -1));
            System.out.println("Collection does contain (-1, -1): " + points.search(new Point(-1, -1)));

            System.out.println("Collection contains (-2, -2): " + points.search(new Point(-2, -2)));
            System.out.println("Replace (-1, -1 with (-2, -2))");
            points.replace(points.size() - 1, new Point(-2, -2));
            System.out.println("Collection contains (-2, -2): " + points.search(new Point(-2, -2)));



        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}

class AddPoints extends Thread {
    private int NUM_OF_POINTS_TO_GENERATE = 10000;
    private int index;
    private CollectionPoint points;

    AddPoints(CollectionPoint points, int index) {
        this.points = points;
        this.index = index;
    }

    /**
     * Generate 2 decimal points random doubles, for easier searching. Hard to hit existing value when testing otherwise
     */
    private double getRandom() {
        return ((int) (Math.random() * 1000)) / 100.d;
    }

    @Override
    public void run() {
        System.out.println("AddPoint thread " + index + " started.");
        for (int i = 0; i < NUM_OF_POINTS_TO_GENERATE; i++) {
            points.add(new Point(getRandom(), getRandom()));
        }
        System.out.println("AddPoint thread " + index + " ended.");
    }
}

//Q1 ===========================================================
class CollectionPoint {
    private List<Point> points = new ArrayList<>();

    synchronized void add(Point p) {
        points.add(p);
    }

    synchronized boolean search(Point point) {
        return points.contains(point);
    }

    synchronized List<Point> getAllX(int x) {
        List<Point> foundPoints = new ArrayList<>();
        for(Point point : points)
            if(point.x() == x)  {
                foundPoints.add(point);
            }
        return foundPoints;
    }

    synchronized void replace(int index, Point point) {
        points.set(index, point);
    }

    synchronized int size() {
        return points.size();
    }

    @Override
    public String toString() {
        return "CollectionPoint{" +
                "points=" + points.toString() +
                '}';
    }
}

final class Point {
    private final double x, y;

    public Point(double x0, double y0) {
        x = x0;
        y = y0;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean equals(Object ob) {
        if (!(ob instanceof Point)) return false;
        Point p = (Point) ob;
        return x == p.x && y == p.y;
    }
}
//End Q1 =======================================================



//Q2 ===========================================================

//End Q2 =======================================================



//Q3 ===========================================================

//End Q3 =======================================================