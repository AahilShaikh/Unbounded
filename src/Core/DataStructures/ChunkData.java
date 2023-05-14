package Core.DataStructures;

import TileEngine.TETile;

import java.io.Serializable;

/**
 * @param FLOOR The floor tile used for this dungeon.
 * @param WALL  The wall tile used for this dungeon.
 * @param BASE  The base tile used for this dungeon.
 */
public record ChunkData(long northChunkSeed, long southChunkSeed, long westChunkSeed,
                        long eastChunkSeed, long chunkSeed, TETile FLOOR, TETile WALL,
                        TETile BASE) implements Serializable {
}
