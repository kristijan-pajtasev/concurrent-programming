import java.util.ArrayList;

/**
 * Assignment5.java
 * Student Name: Kristijan Pajtasev
 * Student Number: 2920266
 */

public class Assignment5 {
    public static void main(String args[]) {
    }
}

class CollectionPoint {
    private ArrayList<Point> points = new ArrayList<>();

    public void add(Point p) {
    }

    public boolean search(Point p) {
        return true;
    }

    public ArrayList<Point> getAllX(int x) {
        return new ArrayList<>();
    }

    public void replace(Point p) {

    }

    @Override
    public String toString() {
        return "CollectionPoint{" +
                "points=" + points +
                '}';
    }
}

final class Point{
    private final double x, y;
    public Point(double x0, double y0){x = x0; y = y0;}
    public double x(){return x;}
    public double y(){return y;}
    public String toString(){return "("+x+","+y+")";}
}
