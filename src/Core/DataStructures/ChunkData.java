package Core.DataStructures;

import TileEngine.TETile;

import java.io.Serializable;
import java.util.Map;


public record ChunkData(long northChunkSeed, long southChunkSeed, long westChunkSeed,
                        long eastChunkSeed, long chunkSeed, Map<String, TETile> tileMap) implements Serializable {
}
