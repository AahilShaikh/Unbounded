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
                System.out.println(noise);
                noise += 1.0;
                noise /= 2.0;
                int c = (int) Math.round(noise * 255);
                c = Math.min(c, 255);
                c = Math.max(c, 0);
                setTile(new Point(x, y), new TETile(' ', Color.WHITE, new Color(c, c, c),"Test " +
                        "tile"));
            }
        }
        return new Chunk(getMap(), getInteractables(), getMobs(), getChunkData(), getRooms());
    }


}
