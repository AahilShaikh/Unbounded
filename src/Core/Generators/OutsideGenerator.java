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
                //TODO
                int halfX = getMap().length / 2;
                int halfY = getMap()[0].length / 2;

                int centerX = getChunkData().getChunkCenter().getX();
                int centerY = getChunkData().getChunkCenter().getY();
                double noise = SimplexNoise.sample(centerX - halfX + x, centerY - halfY + y, 7);
                TETile tile;
                if(noise < -0.5) {
                    tile = getChunkData().getTileMap().get("deep ocean");
                } else if(noise < -0.4) {
                    tile = getChunkData().getTileMap().get("ocean");
                } else if(noise < -0.2) {
                    tile = getChunkData().getTileMap().get("sea");
                } else if(noise < -0.1) {
                    tile = getChunkData().getTileMap().get("beach");
                } else if(noise < 0.2) {
                    tile = getChunkData().getTileMap().get("plains");
                } else if(noise < 0.3) {
                    tile = getChunkData().getTileMap().get("forest");
                } else if(noise < 0.4) {
                    tile = getChunkData().getTileMap().get("deep forest");
                } else if(noise < 0.5) {
                    tile = getChunkData().getTileMap().get("hills");
                } else if(noise < 0.6) {
                    tile = getChunkData().getTileMap().get("cliffs");
                } else if(noise < 0.7) {
                    tile = getChunkData().getTileMap().get("mountains");
                } else if(noise < 0.8) {
                    tile = getChunkData().getTileMap().get("high mountains");
                } else if(noise < 0.9) {
                    tile = getChunkData().getTileMap().get("icy mountains");
                } else {
                    tile = getChunkData().getTileMap().get("ice");
                }

                setTileCopy(new Point(x, y), tile);
            }
        }
        return new Chunk(getMap(), getInteractables(), getMobs(), getChunkData(), getRooms());
    }


}
