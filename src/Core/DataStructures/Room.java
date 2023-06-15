package Core.DataStructures;

import java.io.Serializable;

public class Room implements Serializable {
    private final Point bottomLeft;
    private final int width;
    private final int height;
    private final Point topRight;
    private final Point topLeft;
    private final Point bottomRight;
    private final Point center;

    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    private final int x;
    private final int y;

    private boolean isLocked = false;

    public Room(int startX, int startY, int width, int height) {
        this.x = startX;
        this.y = startY;
        this.bottomLeft = new Point(startX, startY);
        this.bottomRight = new Point(startX + width, startY);
        this.topLeft = new Point(startX, startY + height);
        this.topRight = new Point(startX + width, startY + height);
        this.width = width;
        this.height = height;
        this.center = new Point((startX + (int) ((double) width / 2)),
                (startY + (int) ((double) height / 2)));
        this.left = Math.min(startX, startX + width);
        this.top = Math.min(startY, startY + height);
        this.right = Math.max(startX, startX + width);
        this.bottom = Math.max(startY, startY + height);
    }

    public int distanceFrom(Room other) {
        int vertical;

        if (top >= other.bottom) {
            vertical = top - other.bottom;
        } else if (bottom <= other.top) {
            vertical = other.top - bottom;
        } else {
            vertical = -1;
        }
        int horizontal;
        if (left >= other.right) {
            horizontal = left - other.right;
        } else if (right <= other.left) {
            horizontal = other.left - right;
        } else {
            horizontal = -1;
        }

        if ((vertical == -1) && (horizontal == -1)) {
            return -1;
        }
        if (vertical == -1) {
            return horizontal;
        }
        if (horizontal == -1) {
            return vertical;
        }
        return horizontal + vertical;
    }

    public Point getBottomLeft() {
        return bottomLeft;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Point getTopRight() {
        return topRight;
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public Point getBottomRight() {
        return bottomRight;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point getCenter() {
        return this.center;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public boolean containsPoint(Point p) {
        return (p.x > bottomLeft.x && p.x < bottomRight.x) && (p.y > bottomLeft.y && p.y < topLeft.y);
    }
}
