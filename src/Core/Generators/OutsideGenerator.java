package Core.Generators;

import Core.Chunk;
import Core.Constants;
import Core.DataStructures.ChunkData;
import Core.DataStructures.Point;
import Core.DataStructures.Room;
import Core.DataStructures.Tile;
import Core.Entities.Monster;
import TileEngine.TETile;
import TileEngine.Tileset;
import Utils.SimplexNoise;

import java.awt.*;
import java.lang.constant.Constable;

public class OutsideGenerator extends Generator {
    public OutsideGenerator(int width, int height, ChunkData chunkData) {
        super(width, height, chunkData);
        SimplexNoise.setSeed(chunkData.getChunkSeed());
    }

    @Override
    public Chunk generate() {
        for (int x = 0; x < getMap().length; x++) {
            for (int y = 0; y < getMap()[0].length; y++) {
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
        if(getChunkData().getMobs().isEmpty()) {
            populateMonsters();
        }

        return new Chunk(getMap(), getChunkData(), getRooms());
    }

    private void populateMonsters() {
        int mobCount = 0;
        while (mobCount != 20) {
            int x = getRng().nextInt(0, Constants.STAGE_WIDTH);
            int y = getRng().nextInt(0, Constants.STAGE_HEIGHT);
            Point p = new Point(x, y);
            if (Tileset.reachableEntityTiles.contains(getTile(p))) {
                getChunkData().getMobs().add(new Monster( Tileset.MONSTER.copyOf(), p, 50));
                mobCount++;
            }
        }
    }


}
