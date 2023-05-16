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
                Color c;
                if(noise >= 0.8) {
                    c = Color.WHITE;
                } else if(noise >= 0.28 ) {
                    c =  new Color(136, 110, 88);
                } else if( noise >= 0) {
                    c = new Color(115, 82, 56);
                } else if(noise >= -0.1) {
                    c = new Color(8, 112, 41);
                } else if(noise >= -0.5) {
                    c = new Color(43, 117, 66);
                } else if(noise >= -0.6) {
                    c = new Color(224, 215, 114);
                } else if(noise >= -0.8)  {
                    c = new Color(57, 84, 191);
                } else {
                    c = new Color(26, 60, 196);
                }

                setTile(new Point(x, y), new TETile(' ',
                        Color.WHITE,c,"Test tile"));
            }
        }
        return new Chunk(getMap(), getInteractables(), getMobs(), getChunkData(), getRooms());
    }


}
