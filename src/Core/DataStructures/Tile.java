package Core.DataStructures;

import java.io.Serializable;

/**
 * Wrapper class around a Point object that is used for A* path finding.
 */
public class Tile implements Comparable<Tile>, Serializable {

    private Tile parent = null;
    private final Point coordinates;
    private double gCost;
    private double hCost;

    public Tile(Point coordinates, double gCost, double hCost) {
        this.coordinates = coordinates;
        this.hCost = hCost;
        this.gCost = gCost;

    }

    public Tile(Point coordinates) {
        this.coordinates = coordinates;
        this.hCost = Double.MAX_VALUE;
        this.gCost = Double.MAX_VALUE;

    }

    public double getGCost() {
        return gCost;
    }

    public void setGCost(double newGCost) {
        this.gCost = newGCost;
    }

    public double getHCost() {
        return hCost;
    }

    public void setHCost(double newHCost) {
        this.hCost = newHCost;
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public double getFCost() {
        return gCost + hCost;
    }


    @Override
    public int compareTo(Tile o) {
        return (int) (getFCost() - o.getFCost());
    }

    public Tile getParent() {
        return parent;
    }

    public void setParent(Tile parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tile)) {
            return false;
        }
        return coordinates.equals(((Tile) o).coordinates);
    }

    @Override
    public String toString() {
        return getCoordinates().toString();
    }
}
