package Core.Entities;

import Core.Constants;
import Core.DataStructures.*;
import Core.Chunk;
import Core.GameServices;
import TileEngine.TERenderer;
import TileEngine.TETile;
import TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the game
 */
public class Player implements Entity, Serializable {

    /**
     * An attack of the player.
     * Implements Thread so that concurrent tasks can be fulfilled while the attack moves.
     */
    public static class PlayerAttack extends Thread implements Serializable {
        private final Direction currentlyFacing;
        private final Point currentLoc;
        private final Chunk floorGen;
        private final Player player;

        public PlayerAttack(Direction currentlyFacing, Point currentLoc, Chunk floorGen,
                            Player player) {
            this.currentLoc = currentLoc;
            this.currentlyFacing = currentlyFacing;
            this.floorGen = floorGen;
            this.player = player;
        }

        @Override
        public void run() {
            move();
        }

        private void move() {
            synchronized (floorGen) {
                player.decreaseMana(5);
                int count = 0;
                Direction attackDirection = currentlyFacing;
                Point attackLocation = currentLoc.addDirection(attackDirection, 1);
                Point prevLocation = currentLoc;
                TETile tileCurrentlyOn = Tileset.FLOOR.copyOf();
                while (canMoveTo(attackLocation) && count <= 15) {
                    if (!prevLocation.equals(currentLoc)) {
                        floorGen.setTile(prevLocation, tileCurrentlyOn);
                    }
                    tileCurrentlyOn = floorGen.getTile(attackLocation);
                    floorGen.setTile(attackLocation, Tileset.FLOWER);
                    attackLocation = attackLocation.addDirection(attackDirection, 1);
                    prevLocation = prevLocation.addDirection(attackDirection, 1);
                    count++;
                    TERenderer.getInstance().renderFrame(floorGen.getFloorArray(), floorGen.mobs());
                }
                if (floorGen.getTile(attackLocation.addDirection(attackDirection, -1))
                        .equals(Tileset.FLOWER)) {
                    floorGen.setTile(prevLocation, tileCurrentlyOn);
                }
                //Render attack
                TERenderer.getInstance().renderFrame(floorGen.getFloorArray(),
                         floorGen.mobs());
                attackMonster(attackLocation);
            }
        }

        private boolean canMoveTo(Point p) {
            synchronized (floorGen) {
                return floorGen.isInBounds(p) && Tileset.reachableAttackTiles.contains(floorGen.getTile(p));
            }
        }

        private void attackMonster(Point p) {
            synchronized (floorGen) {
                int index = -1;
                ArrayList<Monster> mobs = new ArrayList<>(floorGen.mobs());
                for (int i = 0; i < mobs.size(); i++) {
                    if (mobs.get(i).sameLocation(p)) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    floorGen.mobs().get(index).getDamaged(player.getDamage());
                }
            }
        }
    }







    private Chunk chunk;
    private final TETile AVATAR;
    private Point currentLoc;
    private Direction currentlyFacing = Direction.UP;
    private int currHealth;
    private final int maxHealth;
    private final int damage = 20;
    private TETile tileCurrentlyOn;
    private int mana;
    private final int maxMana;
    private final List<Interactable> inventory = new ArrayList<>();

    public Player(Chunk chunk, TETile avatar, int health, int mana) {
        this.chunk = chunk;
        this.AVATAR = avatar;
        this.currHealth = health;
        this.maxHealth = health;
        spawn();
        this.mana = mana;
        this.maxMana = mana;
    }

    /**
     * Spawns the player in the current chunk, randomly.
     */
    public void spawn() {
        synchronized (chunk) {
            for(int row = 0; row < this.chunk.map().length; row++) {
                for(int col = 0; col < this.chunk.map()[0].length; col++) {
                    if(canMoveTo(new Point(row, col))) {
                        this.currentLoc = new Point(row, col);
                        this.tileCurrentlyOn = this.chunk.map()[row][col].copyOf();
                        this.chunk.setTileCopy(currentLoc, AVATAR);
                        return;
                    } else {
                        System.out.println("OOPS");
                    }
                }
            }
        }
    }

    /**
     *
     * @param p relative point in chunk
     * @param chunk the chunk you want to spawn the player in
     * @return whether the spawn was successful
     */
    public boolean spawn(Point p, Chunk chunk) {
        synchronized (chunk) {
            System.out.println("IS in bounds: " + chunk.isInBounds(p));
            System.out.println("The tile is: " + chunk.getTile(p));
            if(chunk.isInBounds(p) && Tileset.reachableEntityTiles.contains(chunk.getTile(p))) {
                this.chunk = chunk;
                this.currentLoc = p;
                this.tileCurrentlyOn = chunk.map()[p.getX()][p.getY()].copyOf();
                System.out.println("Tile currently on: " + tileCurrentlyOn);
                this.chunk.setTileCopy(p, AVATAR);
                return true;
            }
            return false;
        }
    }

    @Override
    public void interactWith(List<Interactable> objects) {
        synchronized (chunk) {
            Point location = currentLoc.addDirection(currentlyFacing, 1);
            for (Interactable object : objects) {
                if (object.getLocation().equals(location)) {
                    object.action();
                }
            }
        }

    }

    @Override
    public void attack() {
        if (mana > 0) {
            PlayerAttack attack = new PlayerAttack(currentlyFacing, currentLoc, chunk,
                    this);
            Constants.EXECUTOR_SERVICE.execute(attack);
        }

    }

    @Override
    public void getDamaged(int damageDealt) {
        synchronized (chunk) {
            decreaseHealth(damageDealt);
            chunk.setTile(currentLoc,
                    Tileset.ATTACKED_AVATAR.copyOf().lighten(tileCurrentlyOn.getShade()));
            StdDraw.pause(100);
            if (this.currHealth == 0) {
                chunk.setTile(currentLoc, tileCurrentlyOn);
                GameServices.getInstance().setGameStatus(GameStatus.LOST);
            } else {
                chunk.setTile(currentLoc,
                        AVATAR.copyOf().lighten(tileCurrentlyOn.getShade()));
            }
        }
    }

    @Override
    public boolean canMoveTo(Point p) {
        synchronized (chunk) {
            return chunk.isInBounds(p) && Tileset.reachableEntityTiles.contains(chunk.getTile(p));
        }
    }

    @Override
    public void move(char c) {
        synchronized (chunk) {
            Point location = getCurrentLocation();
            if (c == 'w') {
                currentlyFacing = Direction.UP;
                moveHelper(location.addDirection(Direction.UP, 1));
            } else if (c == 'a') {
                currentlyFacing = Direction.LEFT;
                moveHelper(location.addDirection(Direction.LEFT, 1));
            } else if (c == 's') {
                currentlyFacing = Direction.DOWN;
                moveHelper(location.addDirection(Direction.DOWN, 1));
            } else if (c == 'd') {
                currentlyFacing = Direction.RIGHT;
                moveHelper(location.addDirection(Direction.RIGHT, 1));
            }
        }
    }

    /**
     * Helper function for move, that actually moves the player.
     *
     * @param newLoc The location that the player should move to - should only be one 1 tile away
     *               from the current location of the player.
     */
    private void moveHelper(Point newLoc) {
        synchronized (chunk) {
            if (canMoveTo(newLoc)) {
                chunk.setTile(currentLoc, tileCurrentlyOn);
                tileCurrentlyOn = chunk.getTile(newLoc);
                if(tileCurrentlyOn.equals(Tileset.TREE)) {
                    tileCurrentlyOn = chunk.getChunkData().getTileMap().get("floor").copyOf();
                }
                chunk.setTile(newLoc, AVATAR.copyOf().lighten(tileCurrentlyOn.getShade()));
                currentLoc = newLoc;
            }
        }
    }

    public int getDamage() {
        return this.damage;
    }

    @Override
    public int getHealth() {
        return currHealth;
    }

    @Override
    public void setHealth(int health) {
        currHealth = health;
    }

    public void increaseHealth(int healthGain) {
        currHealth = Math.min(currHealth + healthGain, 100);
    }

    @Override
    public void decreaseHealth(int healthLost) {
        currHealth = Math.max(currHealth - healthLost, 0);
    }

    /**
     * @return Returns the current location of the player inside the current chunk.
     *  (Relative position).
     */
    public Point getCurrentLocation() {
        return currentLoc;
    }

    public TETile getAVATAR() {
        return AVATAR;
    }

    public synchronized TETile getTileCurrentlyOn() {
        return tileCurrentlyOn;
    }

    public synchronized void setTileCurrentlyOn(TETile tileCurrentlyOn) {
        this.tileCurrentlyOn = tileCurrentlyOn;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getMana() {
        return mana;
    }

    public void decreaseMana(int manaLoss) {
        this.mana = Math.max(mana - manaLoss, 0);
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void increaseMana(int manaGain) {
        this.mana = Math.min(mana + manaGain, 100);
    }

    public void addToInventory(Interactable thing) {
        this.inventory.add(thing);
    }

    public void removeFromInventory(Interactable thing) {
        this.inventory.remove(thing);
    }

    public List<Interactable> getInventory() {
        return inventory;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }
}
