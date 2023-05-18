package Core.Generators;

import Core.Chunk;
import Core.DataStructures.ChunkData;
import Core.DataStructures.Point;
import TileEngine.TETile;
import Utils.PerlinNoise;
import Utils.SimplexNoise;

import java.awt.*;

public class OutsideGenerator extends Generator {
    public OutsideGenerator(long seed, int width, int height, ChunkData chunkData) {
        super(seed, width, height, chunkData);
    }

    @Override
    public Chunk generate() {
        for (int x = 0; x < getMap().length; x++) {
            for (int y = 0; y < getMap()[0].length; y++) {
                double noise = SimplexNoise.sample(x, y, 7);
                TETile tile;
                if(noise < -0.5) {
                    tile = getChunkData().tileMap().get("deep ocean");
                } else if(noise < -0.4) {
                    tile = getChunkData().tileMap().get("ocean");
                } else if(noise < -0.2) {
                    tile = getChunkData().tileMap().get("sea");
                } else if(noise < -0.1) {
                    tile = getChunkData().tileMap().get("beach");
                } else if(noise < 0.2) {
                    tile = getChunkData().tileMap().get("plains");
                } else if(noise < 0.3) {
                    tile = getChunkData().tileMap().get("forest");
                } else if(noise < 0.4) {
                    tile = getChunkData().tileMap().get("deep forest");
                } else if(noise < 0.5) {
                    tile = getChunkData().tileMap().get("hills");
                } else if(noise < 0.6) {
                    tile = getChunkData().tileMap().get("cliffs");
                } else if(noise < 0.7) {
                    tile = getChunkData().tileMap().get("mountains");
                } else if(noise < 0.8) {
                    tile = getChunkData().tileMap().get("high mountains");
                } else if(noise < 0.9) {
                    tile = getChunkData().tileMap().get("icy mountains");
                } else {
                    tile = getChunkData().tileMap().get("ice");
                }

                setTileCopy(new Point(x, y), tile);
            }
        }
        return new Chunk(getMap(), getInteractables(), getMobs(), getChunkData(), getRooms());
    }


}
