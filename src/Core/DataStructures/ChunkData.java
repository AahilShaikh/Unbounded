package Core.DataStructures;

import Core.Entities.Interactable;
import Core.Entities.Monster;
import TileEngine.TETile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


public class ChunkData implements Serializable {
    private ChunkData northChunkData = null;
    private ChunkData southChunkData = null;
    private ChunkData westChunkData = null;
    private ChunkData eastChunkData = null;
    private Long chunkSeed;
    private Map<String, TETile> tileMap;
    private ChunkType type;
    private Point chunkCenter;
    private List<Interactable> interactables;
    private List<Monster> mobs;
    public ChunkData(long chunkSeed, Map<String, TETile> tileMap, ChunkType type,
                     Point chunkCenter) {
        this.chunkSeed = chunkSeed;
        this.tileMap = tileMap;
        this.type = type;
        this.chunkCenter = chunkCenter;
        this.interactables = new CopyOnWriteArrayList<>();
        this.mobs = new CopyOnWriteArrayList<>();
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

    public Long getChunkSeed() {
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

    public List<Interactable> getInteractables() {
        return interactables;
    }

    public List<Monster> getMobs() {
        return mobs;
    }
}
