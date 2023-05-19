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
import java.util.Optional;

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
                    TERenderer.getInstance().renderFrame(floorGen.getFloorArray(),
                            player.currentLocation(), player, floorGen.getMobs());
                }
                if (floorGen.getTile(attackLocation.addDirection(attackDirection, -1))
                        .equals(Tileset.FLOWER)) {
                    floorGen.setTile(prevLocation, tileCurrentlyOn);
                }
                //Render attack
                TERenderer.getInstance().renderFrame(floorGen.getFloorArray(),
                        player.currentLocation(), player, floorGen.getMobs());
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
                ArrayList<Monster> mobs = new ArrayList<>(floorGen.getMobs());
                for (int i = 0; i < mobs.size(); i++) {
                    if (mobs.get(i).sameLocation(p)) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    floorGen.getMobs().get(index).getDamaged(player.getDamage());
                }
            }
        }
    }







    private final Chunk floorGen;
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

    public Player(Chunk floorGen, TETile avatar, int health, int mana) {
        this.floorGen = floorGen;
        this.AVATAR = avatar;
        this.currHealth = health;
        this.maxHealth = health;
        Optional<Room> p = this.floorGen.getRooms().stream()
                .filter((Room room) -> !room.isLocked()).findFirst();
        if(p.isPresent()) {
            this.currentLoc = p.get().getCenter();
        } else {
            for(int row = 0; row < this.floorGen.getMap().length; row++) {
                for(int col = 0; col < this.floorGen.getMap()[0].length; col++) {
                    if(canMoveTo(new Point(row, col))) {
                        this.currentLoc = new Point(row, col);
                        this.tileCurrentlyOn = this.floorGen.getMap()[row][col].copyOf();
                    }
                }
            }
        }
        this.floorGen.setTileCopy(currentLoc, AVATAR);
        this.mana = mana;
        this.maxMana = mana;
    }

    @Override
    public void interactWith(List<Interactable> objects) {
        synchronized (floorGen) {
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
            PlayerAttack attack = new PlayerAttack(currentlyFacing, currentLoc, floorGen,
                    this);
            Constants.EXECUTOR_SERVICE.execute(attack);
        }

    }

    @Override
    public void getDamaged(int damageDealt) {
        synchronized (floorGen) {
            decreaseHealth(damageDealt);
            floorGen.setTile(currentLoc,
                    Tileset.ATTACKED_AVATAR.copyOf().lighten(tileCurrentlyOn.getShade()));
            TERenderer.getInstance().renderFrame(floorGen.getFloorArray(), currentLocation(),
                    this, floorGen.getMobs());
            StdDraw.pause(100);
            if (this.currHealth == 0) {
                floorGen.setTile(currentLoc, tileCurrentlyOn);
                GameServices.getInstance().setGameStatus(GameStatus.LOST);
            } else {
                floorGen.setTile(currentLoc,
                        AVATAR.copyOf().lighten(tileCurrentlyOn.getShade()));
            }
            TERenderer.getInstance().renderFrame(floorGen.getFloorArray(), currentLoc, this,
                    floorGen.getMobs());
        }
    }

    @Override
    public boolean canMoveTo(Point p) {
        synchronized (floorGen) {
            return floorGen.isInBounds(p) && Tileset.reachableEntityTiles.contains(floorGen.getTile(p));
        }
    }

    @Override
    public void move(char c) {
        synchronized (floorGen) {
            Point location = currentLocation();
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
            System.out.println(currentLoc);
        }
    }

    /**
     * Helper function for move, that actually moves the player.
     *
     * @param newLoc The location that the player should move to - should only be one 1 tile away
     *               from the current location of the player.
     */
    private void moveHelper(Point newLoc) {
        synchronized (floorGen) {
            System.out.println(canMoveTo(newLoc));
            if (canMoveTo(newLoc)) {
                floorGen.setTile(currentLoc, tileCurrentlyOn);
                tileCurrentlyOn = floorGen.getTile(newLoc);
                if(tileCurrentlyOn.equals(Tileset.TREE)) {
                    tileCurrentlyOn = floorGen.getChunkData().tileMap().get("floor").copyOf();
                }
                floorGen.setTile(newLoc, AVATAR.copyOf().lighten(tileCurrentlyOn.getShade()));
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
     * @return Returns the current location of the player.
     */
    public Point currentLocation() {
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
}
