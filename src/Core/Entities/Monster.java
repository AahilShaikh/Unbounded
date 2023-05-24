package Core.Entities;

import Core.Chunk;
import Core.Constants;
import Core.DataStructures.Direction;
import Core.DataStructures.Point;
import Core.DataStructures.Tile;
import Core.GameServices;
import TileEngine.TERenderer;
import TileEngine.TETile;
import TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Monster implements Entity, Serializable {
    private Chunk chunk;
    private final TETile AVATAR;
    private Point currentLoc;
    private int currHealth;
    private TETile tileCurrentlyOn;

    private boolean isFrozen = true;
    private final int damage = 10;
    private final ArrayList<Point> pathBlocks = new ArrayList<>();
    private boolean showPath = false;

    public Monster(TETile avatar, Point startingLocation, int health) {
        this.AVATAR = avatar;
        currentLoc = startingLocation;
        this.currHealth = health;
    }

    @Override
    public void init(Chunk chunk) {
        this.chunk = chunk;
        this.tileCurrentlyOn = this.chunk.getTile(currentLoc);
        this.chunk.setTileCopy(currentLoc, AVATAR.copyOf());
    }

    @Override
    public void interactWith(List<Interactable> object) {
        throw new UnsupportedOperationException("Monsters cannot interact with objects.");
    }

    @Override
    public void attack() {
        synchronized (chunk) {
            for (Direction dir : Direction.ORDINAL) {
                Point selectedPosition = currentLoc.addDirection(dir, 1);
                if (chunk.isInBounds(selectedPosition) && chunk.getTile(selectedPosition).equals(GameServices.getInstance().getPlayer().getAVATAR())) {
                    GameServices.getInstance().getPlayer().getDamaged(getDamage());
                    break;
                }
            }
        }
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public int getHealth() {
        return currHealth;
    }

    @Override
    public void getDamaged(int damageDealt) {
        synchronized (chunk) {
            this.currHealth -= damageDealt;
            chunk.setTile(currentLoc,
                    Tileset.ATTACKED_MONSTER.copyOf().lighten(tileCurrentlyOn.getShade()));
            TERenderer.getInstance().renderFrame(chunk.getFloorArray(), chunk.getChunkData().getMobs());
            StdDraw.pause(100);
            if (this.currHealth <= 0) {
                hidePath();
                //if the monster has been killed, remove it from the field
                chunk.setTile(currentLoc, tileCurrentlyOn.copyOf().lighten(tileCurrentlyOn.getShade()));
                chunk.getChunkData().getMobs().remove(this);
                chunk.getChunkData().getMobs().forEach((Monster mob) -> {
                    if (!mob.isFrozen) {
                        mob.showPath();
                    }
                });
            } else {
                chunk.setTile(currentLoc, Tileset.MONSTER.copyOf()
                        .lighten(tileCurrentlyOn.getShade()));
            }
            TERenderer.getInstance().renderFrame(chunk.getFloorArray(), chunk.getChunkData().getMobs());
        }
    }

    @Override
    public void setHealth(int health) {
        currHealth = health;
    }

    @Override
    public boolean canMoveTo(Point p) {
        synchronized (chunk) {
            return chunk.isInBounds(p) && Tileset.reachableEntityTiles.contains(chunk.getTile(p));
        }
    }

    public boolean canMove(Point p) {
        synchronized (chunk) {
            return chunk.isInBounds(p) && Tileset.reachableEntityTiles.contains(chunk.getTile(p));
        }
    }


    @Override
    public void move() {
        if (isFrozen && currentLoc.manhattanDistance(GameServices.getInstance().getPlayer().getCurrentLocation()) <= 25) {
            isFrozen = false;
        }
        if (!isFrozen) {
            Thread mover = new Thread(this::moveHelper);
            Constants.EXECUTOR_SERVICE.execute(mover);
        }
    }

    private void moveHelper() {
        synchronized (chunk) {
            hidePath();
            Tile tile = optimalPathToPlayer(currentLoc, GameServices.getInstance().getPlayer().getCurrentLocation());
            if (tile != null) {
                List<Point> moves = new ArrayList<>();
                tile = tile.getParent();
                if (tile == null) {
                    moves.add(tile.getCoordinates());
                } else {
                    while (tile.getParent() != null) {
                        if (showPath) {
                            if (!chunk.getTile(tile.getCoordinates()).equals(AVATAR)) {
                                chunk.setTile(tile.getCoordinates(), Tileset.TREE);
                            }
                        }
                        moves.add(tile.getCoordinates());
                        tile = tile.getParent();
                    }
                    Collections.reverse(moves);
                }
                pathBlocks.addAll(moves);
                if (moves.size() > 0 && canMove(moves.get(0))) {
                    chunk.setTile(currentLoc, tileCurrentlyOn);
                    tileCurrentlyOn = chunk.getTile(moves.get(0));
                    if (tileCurrentlyOn.equals(Tileset.TREE)) {
                        tileCurrentlyOn = chunk.getChunkData().getTileMap().get("floor").copyOf();
                    }
                    chunk.setTile(moves.get(0),
                            Tileset.MONSTER.copyOf().lighten(tileCurrentlyOn.getShade()));
                    currentLoc = moves.get(0);
                }
                chunk.getChunkData().getMobs().forEach((Monster mob) -> {
                    if (!mob.isFrozen) {
                        mob.showPath();
                    }
                });
            }
            attack();
        }
    }

    public void showPath() {
        synchronized (chunk) {
            Tile tile = optimalPathToPlayer(currentLoc, GameServices.getInstance().getPlayer().getCurrentLocation());
            if (tile != null) {
                List<Point> moves = new ArrayList<>();
                tile = tile.getParent();
                if (tile == null) {
                    moves.add(tile.getCoordinates());
                } else {
                    while (tile.getParent() != null) {
                        if (showPath) {
                            if (!chunk.getTile(tile.getCoordinates()).equals(AVATAR)) {
                                chunk.setTile(tile.getCoordinates(), Tileset.TREE);
                            }
                        }
                        moves.add(tile.getCoordinates());
                        tile = tile.getParent();
                    }
                    Collections.reverse(moves);
                }
                pathBlocks.addAll(moves);
            }
        }
    }

    public void hidePath() {
        synchronized (chunk) {
            for (Point p : this.pathBlocks) {
                if (chunk.getTile(p).equals(Tileset.TREE)) {
                    chunk.setTileCopy(p, chunk.getChunkData().getTileMap().get("floor").copyOf());
                }
            }
        }
    }

    private Tile optimalPathToPlayer(Point start, Point end) {
        synchronized (chunk) {
            PriorityQueue<Tile> open = new PriorityQueue<>();
            PriorityQueue<Tile> closed = new PriorityQueue<>();
            Tile startTile = new Tile(start, 0, start.manhattanDistance(end));
            open.add(startTile);
            while (!open.isEmpty()) {
                Tile t = open.peek();
                if (t.getCoordinates().equals(end)) {
                    return t;
                }
                for (Direction dir : Direction.ORDINAL) {
                    Tile x = new Tile(t.getCoordinates().addDirection(dir, 1));
                    if (canMoveTo(x.getCoordinates())) {
                        double weight = t.getGCost() + 1;
                        if (!open.contains(x) && !closed.contains(x)) {
                            x.setParent(t);
                            x.setGCost(weight);
                            x.setHCost(x.getCoordinates().manhattanDistance(end));
                            open.add(x);
                        } else {
                            ArrayList<Tile> openList = new ArrayList<>(open.stream().toList());
                            ArrayList<Tile> closedList = new ArrayList<>(closed.stream().toList());
                            if (openList.contains(x)) {
                                Tile oldTile = openList.get(openList.indexOf(x));
                                x.setGCost(oldTile.getGCost());
                                x.setHCost(oldTile.getHCost());
                            } else if (closedList.contains(x)) {
                                Tile oldTile = closedList.get(closedList.indexOf(x));
                                x.setGCost(oldTile.getGCost());
                                x.setHCost(oldTile.getHCost());
                            }
                            if (weight < x.getGCost()) {
                                x.setParent(t);
                                x.setGCost(weight);
                                x.setHCost(x.getCoordinates().manhattanDistance(end));
                                if (closed.contains(x)) {
                                    closed.remove(x);
                                    open.add(x);
                                }
                            }
                        }
                    }
                }
                open.remove(t);
                closed.add(t);
            }
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Monster) {
            return currentLoc.equals(((Monster) o).currentLoc);
        }
        return false;
    }

    public boolean sameLocation(Point p) {
        return currentLoc.equals(p);
    }

    public Point getCurrentLoc() {
        return currentLoc;
    }

    public void setCurrentLoc(Point currentLoc) {
        this.currentLoc = currentLoc;
    }

    public TETile getTileCurrentlyOn() {
        return tileCurrentlyOn;
    }

    public void setTileCurrentlyOn(TETile tileCurrentlyOn) {
        this.tileCurrentlyOn = tileCurrentlyOn;
    }

    public void changeShowPath() {
        synchronized (chunk) {
            this.showPath = !this.showPath;
            if (!showPath) {
                hidePath();
            } else if (!isFrozen) {
                showPath();
            }
        }
    }
}
