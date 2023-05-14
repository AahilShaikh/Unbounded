package Core.Entities;

import Core.DataStructures.GameStatus;
import Core.DataStructures.Point;
import Core.GameServices;
import TileEngine.TETile;
import TileEngine.Tileset;

import java.io.Serializable;

public class Trophy implements Interactable, Serializable {
    private Point currLoc;
    private final TETile AVATAR = Tileset.TROPHY.copyOf();

    public Trophy(Point currLoc) {
        this.currLoc = currLoc;
    }
    @Override
    public void action() {
        GameServices.getInstance().setGameStatus(GameStatus.WON);
    }
    @Override
    public Point getLocation() {
        return currLoc;
    }

    @Override
    public TETile getTile() {
        return AVATAR;
    }
}
