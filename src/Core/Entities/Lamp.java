package Core.Entities;

import Core.DataStructures.Point;
import Core.FieldOfView;
import Core.Chunk;
import Core.GameServices;
import TileEngine.TERenderer;
import TileEngine.TETile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Lamp implements Interactable, Serializable {
    private Chunk chunk;
    private final int lampStrength = 5;

    private boolean isOn = false;
    private final Point currentLoc;
    private ArrayList<Monster> mobs;
    private Set<Point> reach;
    private Point furthestTile;

    public Lamp(Point currentLoc, ArrayList<Monster> mobs) {
        this.currentLoc = currentLoc;
        this.mobs = mobs;
    }

    @Override
    public void init(Chunk chunk) {
        this.chunk = chunk;
        setReach();
    }

    private void setReach() {
        int bottomX = currentLoc.getX() - lampStrength;
        int bottomY = currentLoc.getY() - lampStrength;
        reach = new HashSet<>();
        FieldOfView fov = new FieldOfView(chunk, lampStrength);
        reach = new HashSet<>(fov.computeFov(currentLoc));
        ArrayList<Point> reached = new ArrayList<>();
        for (Point point : reach) {
            if (!reached.contains(point)) {
                reached.add(point);
            }
        }
        furthestTile = new Point(bottomX, bottomY);
    }

    @Override
    public void action() {
        synchronized (chunk) {
            if (isOn) {
                turnOff();
            } else {
                turnOn();
            }
        }
    }

    private void turnOn() {
        synchronized (chunk) {
            for (Point point : reach) {
                if (chunk.getTile(point).equals(chunk.getChunkData().getTileMap().get("wall"))
                        || chunk.getTile(point).equals(chunk.getChunkData().getTileMap().get("base"))) {
                    continue;
                }
                chunk.getTile(point).lighten(Math.abs(Math
                        .abs(furthestTile.manhattanDistance(currentLoc))
                        - Math.abs(point.manhattanDistance(currentLoc))));

            }
            isOn = true;
            updateEntities();
        }
    }

    private void turnOff() {
        synchronized (chunk) {
            for (Point point : reach) {
                chunk.getTile(point).reset();
            }
            isOn = false;
            updateEntities();
        }
    }


    public void updateEntities() {
        synchronized (chunk) {
            if (isOn) {
                if (reach.contains(GameServices.getInstance().getPlayer().getCurrentLocation())) {
                    GameServices.getInstance().getPlayer().setTileCurrentlyOn(GameServices.getInstance().getPlayer().getTileCurrentlyOn().lighten(Math.abs(Math
                            .abs(furthestTile.manhattanDistance(currentLoc))
                            - Math.abs(GameServices.getInstance().getPlayer().getCurrentLocation().manhattanDistance(currentLoc)))));
                }
                for (Monster mob : mobs) {
                    if (reach.contains(mob.getCurrentLoc())) {
                        mob.getTileCurrentlyOn().lighten(Math.abs(Math
                                .abs(furthestTile.manhattanDistance(currentLoc))
                                - Math.abs(mob.getCurrentLoc().manhattanDistance(currentLoc))));

                    }
                }
            } else {
                GameServices.getInstance().getPlayer().getTileCurrentlyOn().reset();
                for (Monster mob : mobs) {
                    mob.getTileCurrentlyOn().reset();
                }
            }
        }
    }

    @Override
    public Point getLocation() {
        return currentLoc;
    }

    @Override
    public TETile getTile() {
        throw new UnsupportedOperationException();
    }
}
