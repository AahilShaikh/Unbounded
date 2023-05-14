package Core.Entities;

import Core.DataStructures.Point;
import Core.Chunk;
import Core.GameServices;
import TileEngine.TETile;
import TileEngine.Tileset;

import java.io.Serializable;

public class Door implements Interactable, Serializable {
    public enum DoorState {
        LOCKED,
        OPEN;
        private static final DoorState[] VALUES = values();

        public DoorState increase() {
            return VALUES[(this.ordinal() + 1) % VALUES.length];
        }

        public DoorState decrease() {
            return VALUES[(this.ordinal() - 1) % VALUES.length];
        }
    }

    private final Point loc;
    private DoorState state = DoorState.LOCKED;

    private Chunk chunk;

    @Override
    public void init(Chunk chunk) {
        this.chunk = chunk;
    }

    public Door(Point p) {
        this.loc = p;
    }

    @Override
    public void action() {
        //Should change later to be more robust
        if (!GameServices.getInstance().getPlayer().getInventory().isEmpty()) {
            state = state.increase();
            if (state == DoorState.LOCKED) {
                chunk.setTileCopy(loc, Tileset.LOCKED_DOOR);
            } else if (state == DoorState.OPEN) {
                chunk.setTileCopy(loc, Tileset.FLOOR);
            }
        }
    }

    @Override
    public Point getLocation() {
        return loc;
    }

    @Override
    public TETile getTile() {
        throw new UnsupportedOperationException();
    }
}
