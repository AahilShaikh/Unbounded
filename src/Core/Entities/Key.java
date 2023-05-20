package Core.Entities;

import Core.Chunk;
import Core.DataStructures.Point;
import Core.GameServices;
import Core.Generators.Generator;
import TileEngine.TETile;
import TileEngine.Tileset;

import java.io.Serializable;

public class Key implements Interactable, Serializable {
    private final Point currLoc;
    private Chunk chunk;

    private final TETile tile = Tileset.KEY;
    private TETile tileCurrentlyOn;

    public Key(Point currLoc, Generator chunkGen) {
        this.currLoc = currLoc;
    }

    @Override
    public void init(Chunk chunk) {
        this.chunk = chunk;
        tileCurrentlyOn = chunk.getTile(currLoc);
        chunk.setTileCopy(currLoc, Tileset.KEY);
    }

    @Override
    public void action() {
        GameServices.getInstance().getPlayer().addToInventory(this);
        chunk.interactables().remove(this);
        chunk.setTile(currLoc,
                tileCurrentlyOn.copyOf().lighten(chunk.getTile(currLoc).getShade()));
    }

    @Override
    public Point getLocation() {
        return currLoc;
    }

    @Override
    public TETile getTile() {
        return tile;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Key;
    }
}
