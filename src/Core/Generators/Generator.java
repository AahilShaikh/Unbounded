package Core.Generators;

import Core.Chunk;
import Core.DataStructures.ChunkData;
import Core.DataStructures.Direction;
import Core.DataStructures.Point;
import Core.DataStructures.Room;
import Core.Entities.Interactable;
import Core.Entities.Monster;
import TileEngine.TETile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Generator {

    private final Random rng;

    /**
     * 2d array that represents the world.
     */
    private final TETile[][] map;
    /**
     * A list of all the locations of room.
     */
    private final ArrayList<Room> rooms;

    /**
     * List of interactables on the dungeon.
     */
    private final List<Interactable> interactables = new CopyOnWriteArrayList<>();

    private final List<Monster> mobs = new CopyOnWriteArrayList<>();

    private final ChunkData chunkData;

    public Generator(long seed, int width, int height, ChunkData chunkData) {
        rng = new Random(seed);
        this.chunkData = chunkData;
        this.map = new TETile[width][height];
        this.rooms = new ArrayList<>();
    }

    /**
     * Generates the dungeon.
     */
    public Chunk generate() {
        throw new UnsupportedOperationException();
    }

    /**
     * Fills the getMap() with a specific tile.
     * Should be used to create the background.
     *
     * @param tile the tile used to fill the background
     */
    public void fillWith(TETile tile) {
        for (int x = 0; x < getMap().length; x++) {
            for (int y = 0; y < getMap()[0].length; y++) {
                setTileCopy(new Point(x, y), tile);
            }
        }
    }

    /**
     * Checks if the given point p is within bounds of the getMap()
     *
     * @param p point p
     * @return true/false if the value is within bounds of the getMap()
     */
    public boolean isInBounds(Point p) {
        return p.getX() < getMap().length && p.getX() >= 0 && p.getY() < getMap()[0].length && p.getY() >= 0;
    }

    /**
     * Gets the direction that the end point is in relation to the starting point.
     * Only works for points that have moved in only one direction.
     *
     * @param start the starting point
     * @param end   the end point
     * @return the direction of the end point
     */
    public Direction getDirection(Point start, Point end) {
        if (start.getY() < end.getY()) {
            return Direction.UP;
        } else if (start.getY() > end.getY()) {
            return Direction.DOWN;
        } else if (start.getX() < end.getX()) {
            return Direction.RIGHT;
        } else if (start.getX() > end.getX()) {
            return Direction.LEFT;
        } else {
            return null;
        }
    }

    /**
     * Gets the tile at the given point
     *
     * @param p the location to get the tile from
     * @return the tile at the given point
     */
    public TETile getTile(Point p) {
        return getMap()[p.getX()][p.getY()];
    }

    /**
     * Sets the given point to the given tile.
     *
     * @param p    the location to be changed
     * @param tile the tile to change the location to
     */
    public void setTileCopy(Point p, TETile tile) {
        getMap()[p.getX()][p.getY()] = tile.copyOf();
    }

    public void setTile(Point p, TETile tile) {
        getMap()[p.getX()][p.getY()] = tile;
    }

    public TETile[][] getMap() {
        return map;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public List<Interactable> getInteractables() {
        return interactables;
    }

    public List<Monster> getMobs() {
        return mobs;
    }

    public ChunkData getChunkData() {
        return chunkData;
    }

    public Random getRng() {
        return rng;
    }
}
