package Core.Generators;

import Core.Chunk;
import Core.DataStructures.*;
import Core.Entities.*;
import Core.Generators.Generator;
import TileEngine.TETile;
import TileEngine.Tileset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class DungeonGenerator extends Generator {
    /**
     * The amount of tries it will take to generate getRooms().
     * Room generation uses tries instead of a fixed number of getRooms() for performance reasons.
     */
    private final int maxRoomTries;
    
    public DungeonGenerator(long seed, int maxRoomTries, int width, int height, ChunkData chunkData) {
        super(seed, width, height, chunkData);
        this.maxRoomTries = maxRoomTries;
    }

    public Chunk generate() {
        fillWith(getChunkData().BASE());
        //build the dungeon
        addRooms();
        buildPaths();
        createLockedRoom();
        //remove unnecessary tiles
        removeUnnecessaryTiles();
        fillInCorners();
        //populate the monster with entities and getInteractables()
        populateMonsters();
        placeLamps();
        return new Chunk(getMap(), getInteractables(), getMobs(), getChunkData(), getRooms());
    }

    /**
     * Adds getRooms() to the current dungeon. Tries for [maxRoomTries] to place getRooms() randomly.
     * Rooms are assigned sizes and locations randomly. If a room already exists in a location,
     * the new room will not be placed there.
     */
    public void addRooms() {
        for (int i = 0; i < maxRoomTries; i++) {
            //create random room dimensions
            int width = getRng().nextInt(8, 18);
            int height = getRng().nextInt(8, 18);
            int x = getRng().nextInt(0, ((getMap().length - width) / 2) * 2 + 1);
            int y = getRng().nextInt(0, ((getMap()[0].length - height) / 2) * 2 + 1);
            //check if the room overlaps with other getRooms()
            Room room = new Room(x, y, width, height);
            boolean overlap = false;
            for (Room other : getRooms()) {
                if (room.distanceFrom(other) <= 0) {
                    overlap = true;
                    break;
                }
            }
            if (overlap) {
                continue;
            }
            //add getRooms() to list of getRooms() already added
            getRooms().add(room);
            buildRoom(room);
        }
    }

    /**
     * Builds a room based on the given Room object
     *
     * @param room the room to build
     */

    private void buildRoom(Room room) {
        int x = room.getBottomLeft().getX();
        int y = room.getBottomLeft().getY();
        int width = room.getWidth();
        int height = room.getHeight();
        for (int xPos = x; xPos < x + width; xPos++) {
            for (int yPos = y; yPos < y + height; yPos++) {
                if (xPos == room.getX()) {
                    setTileCopy(new Point(xPos, yPos), getChunkData().WALL());
                } else if (xPos >= room.getX() + room.getWidth() - 1) {
                    setTileCopy(new Point(xPos, yPos), getChunkData().WALL());
                } else if (yPos == room.getY()) {
                    setTileCopy(new Point(xPos, yPos), getChunkData().WALL());
                } else if (yPos >= room.getY() + room.getHeight() - 1) {
                    setTileCopy(new Point(xPos, yPos), getChunkData().WALL());
                } else {
                    setTileCopy(new Point(xPos, yPos), getChunkData().FLOOR());
                }
            }
        }
    }

    /**
     * Builds the paths between getRooms(). Each path is built one tile at a time, based on the
     * starting points pythagorean distance from the end point.
     */
    private void buildPaths() {
        for (int q = 0; q < getRooms().size(); q++) {
            Room r1 = getRooms().get(q);
            Room r2 = getRooms().get((q + 1) % getRooms().size());
            Point start = r1.getCenter();
            Point end = r2.getCenter();
            //use prevDir to make the path less windy -> forces paths to make right angle turns
            Direction prevDir = null;
            while (Math.abs(start.distanceFrom(end)) > 0) {
                //Mapping distance from end point to the move
                HashMap<Double, Point> possibleMoves = new HashMap<>();
                for (Direction dir : Direction.ORDINAL) {
                    Point newPoint = start.addDirection(dir, 1);
                    if (isInBounds(newPoint) && !isCorner(newPoint)) {
                        possibleMoves.put(Math.abs(newPoint.distanceFrom(end)), newPoint);
                    }
                }
                double fastestPath = possibleMoves.keySet().stream().sorted().toList().get(0);
                HashMap<Direction, Point> likelyMoves = new HashMap<>();
                Point finalStart = start;
                possibleMoves.forEach((Double num, Point p) -> {
                    if (num >= fastestPath && num <= (int) Math.ceil(fastestPath)) {
                        likelyMoves.put(getDirection(finalStart, p), p);
                    }
                });
                if ((prevDir != null && likelyMoves.get(prevDir) != null)) {
                    start = likelyMoves.get(prevDir);
                } else {
                    prevDir = getDirection(start, possibleMoves.get(fastestPath));
                    start = possibleMoves.get(fastestPath);
                }
                setTileCopy(start, getChunkData().FLOOR());
            }
        }
        buildPathWalls();
    }

    /**
     * Builds the walls of the paths
     */
    private void buildPathWalls() {
        for (int x = 0; x < getMap().length; x++) {
            for (int y = 0; y < getMap()[0].length; y++) {
                Point p = new Point(x, y);
                if (getTile(p).equals(getChunkData().FLOOR().copyOf())) {
                    Point left = p.addDirection(Direction.LEFT, 1);
                    Point up = p.addDirection(Direction.UP, 1);
                    Point right = p.addDirection(Direction.RIGHT, 1);
                    Point down = p.addDirection(Direction.DOWN, 1);
                    if (isInBounds(left) && getTile(left).equals(getChunkData().BASE())) {
                        setTileCopy(left, getChunkData().WALL());
                    }
                    if (isInBounds(right) && getTile(right).equals(getChunkData().BASE())) {
                        setTileCopy(right, getChunkData().WALL());
                    }
                    if (isInBounds(up) && getTile(up).equals(getChunkData().BASE())) {
                        setTileCopy(up, getChunkData().WALL());
                    }
                    if (isInBounds(down) && getTile(down).equals(getChunkData().BASE())) {
                        setTileCopy(down, getChunkData().WALL());
                    }
                }
            }
        }
    }

    /**
     * Chooses one room from the list of getRooms() to lock away and places a key to the room randomly
     * on the dungeon.
     */
    public void createLockedRoom() {
        for (Room room : this.getRooms()) {
            if (areDoorsAllowed(room)) {
                for (int x = room.getX(); x < room.getX() + room.getWidth(); x++) {
                    for (int y = room.getY(); y < room.getY() + room.getHeight(); y++) {
                        Point p = new Point(x, y);
                        Point left1 = p.addDirection(Direction.LEFT, 1);
                        Point left2 = p.addDirection(Direction.LEFT, 2);
                        Point right1 = p.addDirection(Direction.RIGHT, 1);
                        Point right2 = p.addDirection(Direction.RIGHT, 2);
                        Point up1 = p.addDirection(Direction.UP, 1);
                        Point up2 = p.addDirection(Direction.UP, 2);
                        Point down1 = p.addDirection(Direction.DOWN, 1);
                        Point down2 = p.addDirection(Direction.DOWN, 2);
                        if (isInBounds(p) && getTile(p).equals(getChunkData().FLOOR().copyOf())) {
                            if ((isInBounds(left1) && getTile(left1).equals(getChunkData().WALL()))
                                    && (!isInBounds(left2)
                                    || getTile(left2).equals(getChunkData().WALL())) && (isInBounds(right1)
                                    && getTile(right1).equals(getChunkData().WALL())) && (!isInBounds(right2)
                                    || getTile(right2).equals(getChunkData().WALL()))) {
                                setTileCopy(p, Tileset.LOCKED_DOOR);
                                getInteractables().add(new Door(p));
                            } else if ((isInBounds(up1) && getTile(up1).equals(getChunkData().WALL()))
                                    && (!isInBounds(up2)
                                    || getTile(up2).equals(getChunkData().WALL())) && (isInBounds(down1)
                                    && getTile(down1).equals(getChunkData().WALL())) && (!isInBounds(down2)
                                    || getTile(down2).equals(getChunkData().WALL()))) {
                                setTileCopy(p, Tileset.LOCKED_DOOR);
                                getInteractables().add(new Door(p));
                            }
                        }
                    }
                }
                setTile(room.getCenter(), Tileset.TROPHY);
                getInteractables().add(new Trophy(room.getCenter()));
                room.setLocked(true);
                break;
            }
        }
        placeKeys();
    }


    private void placeKeys() {
        int count = 0;
        for (Room room : getRooms()) {
            if (!room.isLocked()) {
                getInteractables().add(new Key(room.getCenter(), this));
                count++;
                if (count > 2) {
                    break;
                }
            }
        }
    }

    /**
     * Fill in corners
     */

    public void fillInCorners() {
        for (int x = 0; x < getMap().length; x++) {
            for (int y = 0; y < getMap()[0].length; y++) {
                Point p = new Point(x, y);
                if (getTile(p).equals(getChunkData().BASE()) && isUnnecessaryCornerTile(p)) {
                    setTileCopy(p, getChunkData().WALL());
                }
            }
        }
    }
    /**
     * Removes any unnecessary tiles that have been placed down.
     * Should be called at the end of generating a level.
     */
    private void removeUnnecessaryTiles() {
        for (int x = 0; x < getMap().length; x++) {
            for (int y = 0; y < getMap()[0].length; y++) {
                Point p = new Point(x, y);
                if (getTile(p).equals(getChunkData().WALL()) && isUnnecessaryWallTile(p)) {
                    setTileCopy(p, getChunkData().FLOOR());
                } else if (getTile(p).equals(getChunkData().BASE()) && isUnnecessaryBaseTile(p)) {
                    setTileCopy(p, getChunkData().WALL());
                }
            }
        }
    }

    private void populateMonsters() {
        for (Room room : getRooms()) {
            int numMonsters = getRng().nextInt(0, 3);
            int count = 0;
            while (count != numMonsters) {
                int x = getRng().nextInt(room.getX(), room.getX() + room.getWidth());
                int y = getRng().nextInt(room.getY(), room.getY() + room.getHeight());
                Point p = new Point(x, y);
                if (getTile(p).equals(getChunkData().FLOOR().copyOf())) {
                    getMobs().add(new Monster( Tileset.MONSTER.copyOf(), p, 50));
                    setTileCopy(p, Tileset.MONSTER);
                    count++;
                }
            }
        }
    }

    public void placeLamps() {
        for (Room room : getRooms()) {
            int numMonsters = getRng().nextInt(0, 2);
            int count = 0;
            while (count != numMonsters) {
                int x = getRng().nextInt(room.getX() + 2, room.getX() + room.getWidth() - 2);
                int y = getRng().nextInt(room.getY() + 2, room.getY() + room.getHeight() - 2);
                Point p = new Point(x, y);
                if (getTile(p).equals(getChunkData().FLOOR().copyOf())) {
                    getInteractables().add(new Lamp(p, new ArrayList<>(getMobs())));
                    setTileCopy(p, Tileset.LIGHT);
                    count++;
                }
            }
        }
    }

    /**
     * Decides whether a wall is needed based on if the wall will be in an open area or if it
     * actually serves as a wall
     *
     * @return whether the wall is needed
     */
    private boolean isUnnecessaryWallTile(Point p) {
        int count = 0;
        boolean isBoundaryWall = false;
        for (Direction dir : Direction.ORDINAL) {
            Point newP = p.addDirection(dir, 1);
            if (isInBounds(newP) && getTile(newP).equals(getChunkData().BASE())) {
                isBoundaryWall = true;
            }
            if (isInBounds(newP) && getTile(newP).equals(getChunkData().FLOOR().copyOf())) {
                count++;
            }
        }
        if (isBoundaryWall && count == 3) {
            return false;
        }
        return count >= 3;
    }

    /**
     * Removes unnecessary base tiles.
     *
     * @param p the tile to remove
     * @return whether the tile should be removed
     */
    private boolean isUnnecessaryBaseTile(Point p) {
        int count = 0;
        for (Direction dir : Direction.ORDINAL) {
            Point newP = p.addDirection(dir, 1);
            if (isInBounds(newP) && getTile(newP).equals(getChunkData().WALL())) {
                count++;
            }

        }
        return count >= 3 && getTile(p).equals(getChunkData().BASE());
    }

    private boolean isUnnecessaryCornerTile(Point p) {
        int count = 0;
        Direction prevDir = null;
        for (Direction dir : Direction.ORDINAL) {
            Point p1 = p.addDirection(dir, 1);
            Point p2 = p.addDirection(dir, 2);
            if(isInBounds(p1) && isInBounds(p2)) {
                if((getTile(p1).equals(getChunkData().WALL())) && getTile(p2).equals(getChunkData().WALL()) && (prevDir == null || Direction.isNeighbor(prevDir, dir))) {
                    count++;
                    prevDir = dir;
                }
            }

        }
        return count == 2 && getTile(p).equals(getChunkData().BASE());
    }

    /**
     * Helper method for determining whether a room should be closed off.
     * Ex.) If the room has to many openings, it would be pointless to cover all of them with
     * doorways. This method is intended to be used by buildDoors().
     *
     * @param room the room to check if doors can be built
     * @return whether doors can be built in the specified room
     */
    private boolean areDoorsAllowed(Room room) {
        int count = 0;
        for (int x = room.getX(); x < room.getX() + room.getWidth(); x++) {
            for (int y = room.getY(); y < room.getY() + room.getHeight(); y++) {
                Point p = new Point(x, y);
                if (getTile(p).equals(getChunkData().WALL())) {
                    count++;
                }
            }
        }
        int expectedNumWalls = (2 * room.getWidth()) + (2 * room.getHeight()) - 4;
        return expectedNumWalls - 2 <= count;
    }

    /**
     * Check if the given point is the corner of a room
     *
     * @param pos the point to check
     * @return returns true or false depending on if the point is a corner
     */
    private boolean isCorner(Point pos) {
        if (!getTile(pos).equals(getChunkData().WALL())) {
            return false;
        }
        Direction prevDir = null;
        for (Direction dir : Direction.ORDINAL) {
            Point tile1 = pos.addDirection(dir, 1);
            if (isInBounds(tile1) && getTile(tile1).equals(getChunkData().WALL())) {
                prevDir = dir;
                break;
            }
        }
        if (prevDir == null) {
            return false;
        } else if (prevDir == Direction.UP || prevDir == Direction.DOWN) {
            Point left = pos.addDirection(Direction.LEFT, 1);
            Point right = pos.addDirection(Direction.RIGHT, 1);
            return (isInBounds(left) || getTile(left).equals(getChunkData().WALL())) && (isInBounds(right)
                    && getTile(right).equals(getChunkData().WALL()));
        } else if (prevDir == Direction.LEFT || prevDir == Direction.RIGHT) {
            Point up = pos.addDirection(Direction.UP, 1);
            Point down = pos.addDirection(Direction.DOWN, 1);
            return (isInBounds(up) && getTile(up).equals(getChunkData().WALL())) || (isInBounds(down)
                    && getTile(down).equals(getChunkData().WALL()));
        }
        return false;
    }
}
