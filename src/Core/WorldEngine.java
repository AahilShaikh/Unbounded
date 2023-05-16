package Core;

import Core.DataStructures.ChunkData;
import Core.Entities.Player;
import Core.Generators.DungeonGenerator;
import Core.Generators.OutsideGenerator;
import TileEngine.TERenderer;
import TileEngine.TETile;
import TileEngine.Tileset;

import java.io.Serializable;
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
     * @param base      The base tile of the floor.
     * @param floor     The floor tile of the floor.
     * @param wall      The wall tile of the floor.
     */
    public void createDungeon(int roomTries, TETile base, TETile floor, TETile wall) {
        long floorSeed = worldEngineRng.nextInt();
        currentChunk = new DungeonGenerator(floorSeed, roomTries,
                TERenderer.getInstance().getStageWidth(),
                TERenderer.getInstance().getStageHeight(), new ChunkData(worldEngineRng.nextInt(),
                worldEngineRng.nextInt(), worldEngineRng.nextInt(),
                worldEngineRng.nextInt(), floorSeed, floor, wall, base)).generate();
    }

    /**
     * Creates a floor.
     *
     * @param base      The base tile of the floor.
     * @param floor     The floor tile of the floor.
     * @param wall      The wall tile of the floor.
     */
    public void createOutside(TETile base, TETile floor, TETile wall) {
        long floorSeed = worldEngineRng.nextInt();
        currentChunk = new OutsideGenerator(floorSeed,
                TERenderer.getInstance().getStageWidth(),
                TERenderer.getInstance().getStageHeight(), new ChunkData(worldEngineRng.nextInt(),
                worldEngineRng.nextInt(), worldEngineRng.nextInt(),
                worldEngineRng.nextInt(), floorSeed, floor, wall, base)).generate();
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
}
