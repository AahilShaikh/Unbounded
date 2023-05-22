package Core;

import Core.DataStructures.ChunkData;
import Core.DataStructures.Direction;
import Core.DataStructures.Point;
import Core.DataStructures.Room;
import Core.Entities.*;
import TileEngine.TETile;
import TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @param map           2d array that represents the world.
 */
public record Chunk(TETile[][] map, ChunkData getChunkData, ArrayList<Room> rooms) implements Serializable {

    public Chunk(TETile[][] map, ChunkData getChunkData, ArrayList<Room> rooms) {
        this.map = map;
        this.getChunkData = getChunkData;
        this.rooms = rooms;

        //init all interactables - includes things like assigning them to this chunk
        for (Interactable i : getChunkData.getInteractables()) {
            i.init(this);
        }
        for (Monster m : getChunkData.getMobs()) {
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

    public boolean isInBounds(char c) {
        Point currLoc = GameServices.getInstance().getPlayer().getCurrentLocation();
        if (c == 'w') {
           return isInBounds( currLoc.addDirection(Direction.UP, 1));
        } else if (c == 'a') {
            return isInBounds( currLoc.addDirection(Direction.LEFT, 1));
        } else if (c == 's') {
            return isInBounds( currLoc.addDirection(Direction.DOWN, 1));
        } else if (c == 'd') {
            return isInBounds( currLoc.addDirection(Direction.RIGHT, 1));
        }
        throw new IllegalArgumentException("Given character is not w, a, s, d");
    }

    public boolean isBlocking(Point p) {
        TETile t = getTile(p);
        return t.equals(getChunkData.getTileMap().get("wall")) || t.equals(Tileset.LOCKED_DOOR);
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

    public ArrayList<Lamp> getLamps() {
        return getChunkData.getInteractables().stream().filter(element -> element instanceof Lamp)
                .map(interactable -> (Lamp) interactable)
                .collect(Collectors.toCollection(ArrayList<Lamp>::new));
    }

    public ArrayList<Key> getKeys() {
        return getChunkData.getInteractables().stream().filter(element -> element instanceof Key)
                .map(interactable -> (Key) interactable)
                .collect(Collectors.toCollection(ArrayList<Key>::new));
    }

    public Trophy getTrophy() {
        return (Trophy) getChunkData.getInteractables().stream().filter(element -> element instanceof Trophy).findFirst().get();
    }

    public ChunkData getChunkData() {
        return getChunkData;
    }
}
