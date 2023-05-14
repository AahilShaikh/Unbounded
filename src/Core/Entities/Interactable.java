package Core.Entities;

import Core.Chunk;
import Core.DataStructures.Point;
import TileEngine.TETile;

public interface Interactable {
    /**
     * Does an action.
     */
    void action();

    Point getLocation();

    TETile getTile();

    default void init(Chunk chunk) {

    }
}
