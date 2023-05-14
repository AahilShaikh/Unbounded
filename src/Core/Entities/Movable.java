package Core.Entities;

import Core.DataStructures.Point;

public interface Movable {
    /**
     * Returns whether the object can move to a specific location
     *
     * @param p the point the object is attempting to move to
     * @return whether the object can move to p
     */
    boolean canMoveTo(Point p);

    /**
     * Move the object to a location
     *
     * @param c the location the object will move to
     */
    default void move(char c) {
    }

    default void move() {
    }
}
