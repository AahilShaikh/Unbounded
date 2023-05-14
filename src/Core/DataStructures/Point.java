package Core.DataStructures;

import java.io.Serializable;

public class Point implements Serializable {
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point addDirection(Direction dir, int num) {
        Point p = new Point(x, y);
        switch (dir) {
            case UP -> p.y += num;
            case DOWN -> p.y -= num;
            case LEFT -> p.x -= num;
            case RIGHT -> p.x += num;
            default -> {
                return p;
            }
        }
        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }
        return x == ((Point) o).x && y == ((Point) o).y;
    }

    public double distanceFrom(Point p) {
        int a, b;
        int sign = 1;
        if (p.y < y) {
            sign *= -1;
        }
        b = p.y - y;
        if (p.x < x) {
            sign *= -1;
        }
        a = p.x - x;
        return sign * Math.sqrt((b * b) + (a * a));
    }

    public int manhattanDistance(Point p) {
        return Math.abs((x - p.x)) + Math.abs(y - p.y);
    }


    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        return x * 31 * y;
    }
}
