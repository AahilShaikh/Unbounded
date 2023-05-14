package TileEngine;

import Core.DataStructures.Point;
import Core.Entities.Interactable;
import Core.Entities.Monster;
import Core.Entities.Player;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

/**
 * Utility class for rendering tiles.
 */
public class TERenderer implements Serializable {
    private static final int TILE_SIZE = 16;
    private int viewportWidth;
    private int viewportHeight;

    private int stageWidth;
    private int stageHeight;
    private int xOffset;
    private int yOffset;
    private Point center;

    private boolean isOn = true;

    public static TERenderer instance;

    private TERenderer(boolean isOn) {
        this.isOn = isOn;
    }

    public static TERenderer getInstance() {
        if(instance == null) {
            instance = new TERenderer(true);
        }
        return instance;
    }

    public static void setInstance(TERenderer renderer) {
        instance = renderer;
    }

    /**
     * Same functionality as the other initialization method. The only difference is that the xOff
     * and yOff parameters will change where the renderFrame method starts drawing. For example,
     * if you select w = 60, h = 30, xOff = 3, yOff = 4 and then call renderFrame with a
     * TETile[50][25] array, the renderer will leave 3 tiles blank on the left, 7 tiles blank
     * on the right, 4 tiles blank on the bottom, and 1 tile blank on the top.
     *
     * @param viewportWidth width of the window in tiles
     * @param viewportHeight height of the window in tiles.
     */
    public void initialize(int viewportWidth, int viewportHeight, int stageWidth, int stageHeight,
                           int xOff,
                           int yOff) {
        if (isOn) {
            this.viewportWidth = viewportWidth;
            this.viewportHeight = viewportHeight;
            this.stageWidth = stageWidth;
            this.stageHeight = stageHeight;
            this.xOffset = xOff;
            this.yOffset = yOff;
            StdDraw.setCanvasSize(this.viewportWidth * TILE_SIZE, this.viewportHeight * TILE_SIZE);
            Font font = new Font("Monaco", Font.BOLD, TILE_SIZE - 2);
            StdDraw.setFont(font);
            StdDraw.setXscale(0, this.viewportWidth);
            StdDraw.setYscale(0, this.viewportHeight);

            StdDraw.clear(new Color(0, 0, 0));

            StdDraw.enableDoubleBuffering();
            StdDraw.show();
        }
    }

    /**
     * Takes in a 2d array of TETile objects and renders the 2d array to the screen, starting from
     * xOffset and yOffset.
     * <p>
     * If the array is an NxM array, then the element displayed at positions would be as follows,
     * given in units of tiles.
     * <p>
     * positions   xOffset |xOffset+1|xOffset+2| .... |xOffset+world.length
     * <p>
     * startY+world[0].length   [0][M-1] | [1][M-1] | [2][M-1] | .... | [N-1][M-1]
     * ...    ......  |  ......  |  ......  | .... | ......
     * startY+2    [0][2]  |  [1][2]  |  [2][2]  | .... | [N-1][2]
     * startY+1    [0][1]  |  [1][1]  |  [2][1]  | .... | [N-1][1]
     * startY    [0][0]  |  [1][0]  |  [2][0]  | .... | [N-1][0]
     * <p>
     * By varying xOffset, yOffset, and the size of the screen when initialized, you can leave
     * empty space in different places to leave room for other information, such as a GUI.
     * This method assumes that the xScale and yScale have been set such that the max x
     * value is the width of the screen in tiles, and the max y value is the height of
     * the screen in tiles.
     *
     * @param world the 2D TETile[][] array to render
     */
    public synchronized void renderTiles(TETile[][] world) {
        if (isOn) {
            StdDraw.setFont(new Font("Monaco", Font.BOLD, TILE_SIZE - 2));
            int numXTiles = world.length;
            int numYTiles = world[0].length;
            StdDraw.clear(new Color(0, 0, 0));
            for (int x = 0; x < numXTiles; x += 1) {
                for (int y = 0; y < numYTiles; y += 1) {
                    if (world[x][y] == null) {
                        throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                                + " is null.");
                    }
                    world[x][y].draw(x + xOffset, y + yOffset);
                }
            }
            StdDraw.show();
        }
    }


    public synchronized void renderFrame(TETile[][] currentFloorArray, Point p, Player player,
                                         List<Monster> mobs) {
        if (isOn) {
            TETile[][] viewPort = centerViewPort(currentFloorArray, p);
            updateGUI(player, mobs, viewPort);
        }
    }

    public synchronized void renderFrame(TETile[][] currentFloorArray, Player player,
                                         List<Monster> mobs) {
        if (isOn) {
            TETile[][] viewPort = centerViewPort(currentFloorArray);
            updateGUI(player, mobs, viewPort);
        }
    }

    public synchronized TETile[][] centerViewPort(TETile[][] currentFloorArray, Point p) {
        this.center = p;
        TETile[][] viewPortArray = new TETile[viewportWidth][viewportHeight - yOffset];
        int x = p.getX() - (viewportWidth / 2);
        int y = p.getY() - (viewportHeight / 2);

        if (x < 0) {
            x += -x;
        }
        if (y < 0) {
            y += -y;
        }
        if (x + viewportWidth > currentFloorArray.length) {
            x -= (x + viewportWidth - currentFloorArray.length);
        }
        if (y + viewportHeight - yOffset > currentFloorArray[0].length) {
            y -= (y + viewportHeight - yOffset - (currentFloorArray[0].length));
        }
        for (int row = x; row < x + viewportWidth; row++) {
            System.arraycopy(currentFloorArray[row], y, viewPortArray[row - x], 0, viewportHeight - yOffset);
        }

        renderTiles(viewPortArray);
        return viewPortArray;
    }

    public synchronized TETile[][] centerViewPort(TETile[][] currentFloorArray) {
        return centerViewPort(currentFloorArray, center);
    }


    public synchronized void updateGUI(Player player, List<Monster> mobs, TETile[][] arr) {
        if (isOn) {
            StdDraw.setPenRadius(0.005);
            StdDraw.setPenColor(Color.DARK_GRAY);
            StdDraw.line(0, yOffset, viewportWidth, yOffset);
            updateHealthBar(player);
            updateManaBar(player);
            updateEnemyCounter(mobs);
            updateMouseTile(arr);
            updateInventory(player);
            StdDraw.show();
        }

    }

    public synchronized void updateHealthBar(Player player) {
        if (isOn) {
            int x = 3;
            int y = 4;
            StdDraw.setPenColor(Color.RED);
            StdDraw.textLeft(x, y, "Health");
            StdDraw.setPenRadius(0.01);
            double healthPercent = (double) player.getHealth() / player.getMaxHealth();
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.line(x + 4, y, x + 14, y);
            StdDraw.setPenColor(Color.RED);
            StdDraw.line(x + 4, y, x + 4 + (healthPercent * 10), y);
            StdDraw.textLeft(15 + x, y, player.getHealth() + " / " + player.getMaxHealth());
        }
    }

    public synchronized void updateManaBar(Player player) {
        if (isOn) {
            int bottomY = 2;
            int bottomX = 3;
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.textLeft(bottomX, bottomY, "Mana");
            StdDraw.setPenRadius(0.01);
            double manaPercent = (double) player.getMana() / player.getMaxMana();
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.line(bottomX + 4, bottomY, bottomX + 14, bottomY);
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.line(bottomX + 4, bottomY, bottomX + 4 + (manaPercent * 10), bottomY);
            StdDraw.textLeft(15 + bottomX, bottomY, player.getMana() + " / " + player.getMaxMana());
        }
    }

    public synchronized void updateEnemyCounter(List<Monster> mobs) {
        if (isOn) {
            int numMobs = mobs.size();
            int x = 24;
            int y = 3;
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.textLeft(x, y, "Monsters left: " + numMobs);
        }
    }

    public synchronized void updateMouseTile(TETile[][] arr) {
        if (isOn) {
            int x = (int) Math.round(StdDraw.mouseX());
            int y = (int) Math.round(StdDraw.mouseY() - yOffset);
            if (x >= arr.length || y >= arr[0].length || x < 0 || y < 0) {
                return;
            }
            String tile = arr[x][y].description();
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.textRight(viewportWidth - 2, 3, tile);
        }
    }

    public synchronized void updateInventory(Player player) {
        if (isOn) {
            int x = 35;
            int y = 4;
            for (Interactable i : player.getInventory()) {
                StdDraw.setPenColor(i.getTile().getTextColor());
                StdDraw.setPenRadius(0.04);
                StdDraw.text(x, y, String.valueOf(i.getTile().getCharacter()));
                y -= 2;
            }
        }
    }
    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public void setViewportWidth(int viewportWidth) {
        this.viewportWidth = viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }

    public void setViewportHeight(int viewportHeight) {
        this.viewportHeight = viewportHeight;
    }

    public int getStageWidth() {
        return stageWidth;
    }

    public void setStageWidth(int stageWidth) {
        this.stageWidth = stageWidth;
    }

    public int getStageHeight() {
        return stageHeight;
    }

    public void setStageHeight(int stageHeight) {
        this.stageHeight = stageHeight;
    }
}
