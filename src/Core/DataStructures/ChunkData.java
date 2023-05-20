package Core.DataStructures;

import TileEngine.TETile;

import java.io.Serializable;
import java.util.Map;


public class ChunkData implements Serializable {
    private ChunkData northChunkData = null;
    private ChunkData southChunkData = null;
    private ChunkData westChunkData = null;
    private ChunkData eastChunkData = null;
    private long chunkSeed;
    private Map<String, TETile> tileMap;
    private ChunkType type;
    private Point chunkCenter;

    public ChunkData(long chunkSeed, ChunkData northChunkData, ChunkData southChunkData,
                     ChunkData westChunkData, ChunkData eastChunkData, Map<String, TETile> tileMap,
                     ChunkType type, Point chunkCenter) {
        this.northChunkData = northChunkData;
        this.southChunkData = southChunkData;
        this.westChunkData = westChunkData;
        this.eastChunkData = eastChunkData;
        this.chunkSeed = chunkSeed;
        this.tileMap = tileMap;
        this.type = type;
        this.chunkCenter = chunkCenter;
    }
    public ChunkData(long chunkSeed, Map<String, TETile> tileMap, ChunkType type, Point chunkCenter) {
        this.chunkSeed = chunkSeed;
        this.tileMap = tileMap;
        this.type = type;
        this.chunkCenter = chunkCenter;
    }

    public enum ChunkType {
        DUNGEON,
        OUTSIDE
    }

    public ChunkData getNorthChunkData() {
        return northChunkData;
    }

    public void setNorthChunkData(ChunkData northChunkData) {
        this.northChunkData = northChunkData;
    }

    public ChunkData getSouthChunkData() {
        return southChunkData;
    }

    public void setSouthChunkData(ChunkData southChunkData) {
        this.southChunkData = southChunkData;
    }

    public ChunkData getWestChunkData() {
        return westChunkData;
    }

    public void setWestChunkData(ChunkData westChunkData) {
        this.westChunkData = westChunkData;
    }

    public ChunkData getEastChunkData() {
        return eastChunkData;
    }

    public void setEastChunkData(ChunkData eastChunkData) {
        this.eastChunkData = eastChunkData;
    }

    public long getChunkSeed() {
        return chunkSeed;
    }

    public void setChunkSeed(long chunkSeed) {
        this.chunkSeed = chunkSeed;
    }

    public Map<String, TETile> getTileMap() {
        return tileMap;
    }

    public void setTileMap(Map<String, TETile> tileMap) {
        this.tileMap = tileMap;
    }

    public ChunkType getType() {
        return type;
    }

    public void setType(ChunkType type) {
        this.type = type;
    }

    public Point getChunkCenter() {
        return chunkCenter;
    }

    public void setChunkCenter(Point chunkCenter) {
        this.chunkCenter = chunkCenter;
    }
}
