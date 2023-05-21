package Core;

import Core.DataStructures.ChunkData;
import Core.DataStructures.Point;
import Core.Entities.Player;
import Core.Generators.DungeonGenerator;
import Core.Generators.OutsideGenerator;
import TileEngine.TERenderer;
import TileEngine.TETile;
import TileEngine.Tileset;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Random;

/**
 * Responsible for creating the world, view mechanics (scrolling), and populating with the player
 * and monsters
 */
public class WorldEngine implements Serializable {
    private Chunk currentChunk;

    /**
     * The seed used for world generation.
     */
    private long worldGenSeed;

    /**
     * The random number generator used for procedural world generation.
     */
    private Random worldEngineRng;

    /**
     * Constructor used to create the world engine.
     *
     * @param worldGenSeed   The seed used for rng.
     * @param stageWidth  The width of the screen.
     * @param stageHeight The height of the screen.
     */
    WorldEngine(long worldGenSeed, int stageWidth, int stageHeight, int viewPortWidth, int viewPortHeight) {
        this.worldGenSeed = worldGenSeed;
        this.worldEngineRng = new Random(worldGenSeed);
        TERenderer.getInstance().initialize(viewPortWidth, viewPortHeight, stageWidth,
                stageHeight, 0, Constants.GUI_HEIGHT);
    }

    /**
     * Creates a floor.
     *
     * @param roomTries The number of times a room should be attempted to be placed on the floor.
     */
    public Chunk createDungeon(int roomTries, Point chunkLocation) {
        long floorSeed = worldEngineRng.nextInt();
        Map<String, TETile> tileMap = Map.ofEntries(
                new AbstractMap.SimpleEntry<>("floor", Tileset.FLOOR),
                new AbstractMap.SimpleEntry<>("wall", Tileset.WALL),
                new AbstractMap.SimpleEntry<>("base", Tileset.NOTHING)
        );
        return new DungeonGenerator(floorSeed, roomTries,
                TERenderer.getInstance().getStageWidth(),
                TERenderer.getInstance().getStageHeight(), new ChunkData(floorSeed,
                tileMap, ChunkData.ChunkType.DUNGEON, chunkLocation)).generate();
    }

    /**
     * Creates a floor.
     */
    public Chunk createOutside(Point chunkLocation) {
        long floorSeed = worldEngineRng.nextInt();
        Map<String, TETile> tileMap = Map.ofEntries(
                new AbstractMap.SimpleEntry<>("deep ocean", Tileset.DEEP_OCEAN),
                new AbstractMap.SimpleEntry<>("ocean", Tileset.OCEAN),
                new AbstractMap.SimpleEntry<>("sea", Tileset.SEA),
                new AbstractMap.SimpleEntry<>("beach", Tileset.BEACH),
                new AbstractMap.SimpleEntry<>("plains", Tileset.PLAINS),
                new AbstractMap.SimpleEntry<>("forest", Tileset.FOREST),
                new AbstractMap.SimpleEntry<>("deep forest", Tileset.DEEP_FOREST),
                new AbstractMap.SimpleEntry<>("hills", Tileset.HILLS),
                new AbstractMap.SimpleEntry<>("cliffs", Tileset.CLIFFS),
                new AbstractMap.SimpleEntry<>("mountains", Tileset.MOUNTAINS),
                new AbstractMap.SimpleEntry<>("high mountains", Tileset.HIGH_MOUNTAINS),
                new AbstractMap.SimpleEntry<>("icy mountains", Tileset.ICY_MOUNTAINS),
                new AbstractMap.SimpleEntry<>("ice", Tileset.ICE)
        );
        return new OutsideGenerator(floorSeed,
                TERenderer.getInstance().getStageWidth(),
                TERenderer.getInstance().getStageHeight(),
                new ChunkData(floorSeed, tileMap, ChunkData.ChunkType.OUTSIDE, chunkLocation)).generate();

    }

    public void tileNextChunk(char direction) {
        if(direction == 'w') {
            Chunk nextChunk = createOutside(currentChunk.getChunkData().getChunkCenter().add(0,
                    Constants.STAGE_HEIGHT));
            Point currPos = GameServices.getInstance().getPlayer().getCurrentLocation();
            Point newPosInChunk = new Point(currPos.getX(), 0);
            if(GameServices.getInstance().getPlayer().spawn(newPosInChunk, nextChunk)) {
                currentChunk.getChunkData().setNorthChunkData(nextChunk.getChunkData());
                nextChunk.getChunkData().setSouthChunkData(currentChunk.getChunkData());
                currentChunk = nextChunk;
            }
        } else if (direction == 's') {
            Chunk nextChunk = createOutside(currentChunk.getChunkData().getChunkCenter().add(0,
                    -Constants.STAGE_HEIGHT));
            Point currPos = GameServices.getInstance().getPlayer().getCurrentLocation();
            Point newPosInChunk = new Point(currPos.getX(), Constants.STAGE_HEIGHT - 1);
            if(GameServices.getInstance().getPlayer().spawn(newPosInChunk, nextChunk)) {
                currentChunk.getChunkData().setSouthChunkData(nextChunk.getChunkData());
                nextChunk.getChunkData().setNorthChunkData(currentChunk.getChunkData());
                currentChunk = nextChunk;
            }
        } else if (direction == 'a') {
            Chunk nextChunk =
                    createOutside(currentChunk.getChunkData().getChunkCenter().add(
                            -Constants.STAGE_WIDTH,0));
            Point currPos = GameServices.getInstance().getPlayer().getCurrentLocation();
            Point newPosInChunk = new Point(Constants.STAGE_WIDTH - 1 , currPos.getY());
            if(GameServices.getInstance().getPlayer().spawn(newPosInChunk, nextChunk)) {
                currentChunk.getChunkData().setWestChunkData(nextChunk.getChunkData());
                nextChunk.getChunkData().setEastChunkData(currentChunk.getChunkData());
                currentChunk = nextChunk;
            }
        } else if(direction =='d') {
            Chunk nextChunk =
                    createOutside(currentChunk.getChunkData().getChunkCenter().add(
                            Constants.STAGE_WIDTH,0));
            Point currPos = GameServices.getInstance().getPlayer().getCurrentLocation();
            Point newPosInChunk = new Point(0 , currPos.getY());
            if(GameServices.getInstance().getPlayer().spawn(newPosInChunk, nextChunk)) {
                currentChunk.getChunkData().setEastChunkData(nextChunk.getChunkData());
                nextChunk.getChunkData().setWestChunkData(currentChunk.getChunkData());
                currentChunk = nextChunk;
            }
        }
    }


    /**
     * Creates a new player.
     */
    public void createPlayer() {
        GameServices.getInstance().setPlayer(new Player(currentChunk, Tileset.AVATAR.copyOf(), 100, 100));
    }

    /**
     * @return Returns the random number generator
     */
    public long getWorldGenSeed() {
        return worldGenSeed;
    }

    /**
     * Sets the random number generator to the given random number generator.
     *
     * @param seed The new random number generator.
     */
    public void setWorldEngineRng(long seed) {
        this.worldGenSeed = seed;
        this.worldEngineRng = new Random(seed);
    }

    public Chunk getCurrentChunk() {
        return currentChunk;
    }

    public void setCurrentChunk(Chunk currentChunk) {
        this.currentChunk = currentChunk;
    }
}
