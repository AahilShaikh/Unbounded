package Core.DataStructures;

import java.io.Serializable;

/**
 * Enum used to represent the cardinal directions
 */

public enum Direction implements Serializable {
    LEFT,
    RIGHT,
    UP,
    DOWN;
    /**
     * Ordinal directions
     */
    public static final Direction[] ORDINAL = {
        Direction.UP,
        Direction.DOWN,
        Direction.LEFT,
        Direction.RIGHT
    };

    public static boolean isNeighbor(Direction dir, Direction neighbor) {
        if(dir == Direction.UP || dir == Direction.DOWN) {
            return  neighbor == Direction.LEFT || neighbor == Direction.RIGHT;
        }else {
            return neighbor == Direction.DOWN || neighbor == Direction.UP;
        }
    }
}
