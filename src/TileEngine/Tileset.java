package TileEngine;

import java.awt.*;
import java.io.File;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 * <p>
 * Ex:
 * world[x][y] = Tileset.FLOOR;
 * <p>
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {

    public static final Color baseColor = new Color(2, 2, 28);
    public static final Color wallColor = new Color(50, 56, 68);
    public static final TETile AVATAR = new TETile('@', Color.white, baseColor, "Player");
    public static final TETile ATTACKED_AVATAR = new TETile('@', Color.PINK, baseColor, "Player");
    public static final TETile WALL = new TETile('|', wallColor, wallColor,
            "Wall");
    public static final TETile FLOOR = new TETile('·', Color.WHITE, baseColor, "Floor");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "Nothing");
    public static final TETile GRASS = new TETile('"', Color.green, baseColor, "grass");
    public static final TETile KEY = new TETile('‡', Color.YELLOW, baseColor, "Key");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, baseColor,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, baseColor,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, baseColor, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, baseColor, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, baseColor, "tree");
    public static final TETile MONSTER = new TETile('M', Color.RED, baseColor, "Monster",
            "./src/Assets/monster.png");
    public static final TETile ATTACKED_MONSTER = new TETile('M', Color.WHITE, baseColor,
            "Monster", "./src/Assets/attackedMonster.png");
    public static final TETile LIGHT = new TETile('O', Color.WHITE, baseColor,
            "Lamp");
    public static final TETile TROPHY = new TETile('þ', Color.YELLOW, baseColor,
            "Trophy");
    public static final TETile TEST_SQUARE = new TETile(' ', Color.WHITE, baseColor, "Test tile" );
}


