/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Saadu
 */
public class Database {
    List<Coordinates> allpoints;
    List<Coordinates> boundaryp;
    List<Coordinates> innerp;
    Integer x1,x2,x3,x4,y1,y2,y3,y4;

    public Database() {
        this.allpoints = new ArrayList<>();
        this.innerp = new ArrayList<>();
        this.boundaryp = new ArrayList<>();
    }
    
    public void sortBoundaryPoints() {
        // Define a custom comparator to sort by x first and then by y
        Comparator<Coordinates> comparator = Comparator.comparing(Coordinates::getY)
                .thenComparing(Coordinates::getX);

        // Sort the boundaryp list using the custom comparator
        Collections.sort(boundaryp, comparator);
    }
    public void sortAllPoints() {
        // Define a custom comparator to sort by x first and then by y
        Comparator<Coordinates> comparator = Comparator.comparing(Coordinates::getX)
                .thenComparing(Coordinates::getY);

        // Sort the boundaryp list using the custom comparator
        Collections.sort(allpoints, comparator);
    }
    
    public boolean add(int x, int y) {
        Coordinates xy = new Coordinates(x, y);
        if (!this.allpoints.contains(xy)){
            this.allpoints.add(xy);
            sortAllPoints();
            return true;
        }
        return false;
    }
    public boolean addBoundaryPoint(Coordinates xy) {
        Coordinates last = boundaryp.getLast();
        if (last.getX() != xy.getX() && last.getY() != xy.getY()){
            this.allpoints.add(xy);
            return true;
        }
        return false;
    }
    
    public List<Coordinates> getPoints() {
        return this.allpoints;
    }
    
    public void clearConvexHull() {
        this.boundaryp.clear();
    }
    
    public void clearAllPoints() {
        this.allpoints.clear();
    }
    
}
class Coordinates {
    int x;
    int y;
    
    public Coordinates(int x, int y) 
    {
        this.x = x;
        this.y = y;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public static int ccw(double x1,double y1,double x2,double y2,double x3,double y3)
    {
        System.out.println("Method: Counter Clockwise Test ");
        double area2 = (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
        if (area2 < 0) return -1;
        else if (area2 > 0) return 1;
        else return 0;
    }
    public static int ccw(int x1,int y1,int x2,int y2,int x3,int y3)
    {
        double area2 = (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
        if (area2 < 0) return -1;
        else if (area2 > 0) return 1;
        else return 0;
    }
}
