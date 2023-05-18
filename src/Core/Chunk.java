package Core;

import Core.DataStructures.ChunkData;
import Core.DataStructures.Point;
import Core.DataStructures.Room;
import Core.Entities.*;
import TileEngine.TETile;
import TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Chunk implements Serializable {

    private final ChunkData chunkData;
    /**
     * 2d array that represents the world.
     */
    private final TETile[][] map;

    /**
     * List of interactables on the dungeon.
     */
    private final List<Interactable> interactables;

    private final List<Monster> mobs;

    private final ArrayList<Room> rooms;

    public Chunk(TETile[][] map, List<Interactable> interactables, List<Monster> mobs,
          ChunkData chunkData, ArrayList<Room> rooms) {
        this.map = map;
        this.interactables = interactables;
        this.mobs = mobs;
        this.chunkData = chunkData;
        this.rooms = rooms;

        //init all interactables - includes things like assigning them to this chunk
        for (Interactable i : interactables) {
            i.init(this);
        }
        for(Monster m : mobs) {
            m.init(this);
        }
    }

    /**
     * Sets the given point to the given tile.
     *
     * @param p    the location to be changed
     * @param tile the tile to change the location to
     */
    public void setTileCopy(Point p, TETile tile) {
        map[p.getX()][p.getY()] = tile.copyOf();
    }

    public void setTile(Point p, TETile tile) {
        map[p.getX()][p.getY()] = tile;
    }

    /**
     * Checks if the given point p is within bounds of the map
     *
     * @param p point p
     * @return true/false if the value is within bounds of the map
     */
    public boolean isInBounds(Point p) {
        return p.getX() < map.length && p.getX() >= 0 && p.getY() < map[0].length && p.getY() >= 0;
    }

    public boolean isBlocking(Point p) {
        TETile t = getTile(p);
        return t.equals(chunkData.tileMap().get("wall")) || t.equals(Tileset.LOCKED_DOOR);
    }

    /**
     * Gets the tile at the given point
     *
     * @param p the location to get the tile from
     * @return the tile at the given point
     */
    public TETile getTile(Point p) {
        return map[p.getX()][p.getY()];
    }

    /**
     * @return Returns the dungeon
     */
    public TETile[][] getFloorArray() {
        return this.map;
    }

    /**
     * @return Returns the list of interactable tiles on this dungeon.
     */
    public List<Interactable> getInteractables() {
        return interactables;
    }

    public synchronized List<Monster> getMobs() {
        return mobs;
    }

    public ArrayList<Lamp> getLamps() {
        return interactables.stream().filter(element -> element instanceof Lamp)
                .map(interactable -> (Lamp) interactable)
                .collect(Collectors.toCollection(ArrayList<Lamp>::new));
    }

    public ArrayList<Key> getKeys() {
        return interactables.stream().filter(element -> element instanceof Key)
                .map(interactable -> (Key) interactable)
                .collect(Collectors.toCollection(ArrayList<Key>::new));
    }

    public Trophy getTrophy() {
        return (Trophy) interactables.stream().filter(element -> element instanceof Trophy).findFirst().get();
    }

    public ChunkData getChunkData() {
        return chunkData;
    }

    public TETile[][] getMap() {
        return map;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }
}
