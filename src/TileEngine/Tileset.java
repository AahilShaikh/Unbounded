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
    //Colors
    public static final Color baseColor = new Color(2, 2, 28);
    public static final Color wallColor = new Color(50, 56, 68);
    public static final TETile AVATAR = new TETile('@', Color.white, new Color(0, 0, 0, 0),
            "Player");
    //Monster tiles
    public static final TETile ATTACKED_AVATAR = new TETile('@', Color.PINK, baseColor, "Player");
    public static final TETile MONSTER = new TETile('M', Color.RED, baseColor, "Monster",
            "./src/Assets/monster.png");
    public static final TETile ATTACKED_MONSTER = new TETile('M', Color.WHITE, baseColor,
            "Monster", "./src/Assets/attackedMonster.png");

    //Dungeon tiles
    public static final TETile FLOOR = new TETile('·', Color.WHITE, baseColor, "Floor");
    public static final TETile WALL = new TETile('|', wallColor, wallColor,
            "Wall");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "Nothing");

    //Outside tiles
    public static final TETile DEEP_OCEAN = new TETile(' ',Color.WHITE,new Color(1, 38, 119),
            "Grass");
    public static final TETile OCEAN = new TETile(' ',Color.WHITE,new Color(0, 91, 197),
            "Grass");
    public static final TETile SEA = new TETile(' ',Color.WHITE,new Color(0, 180, 252),
            "Grass");
    public static final TETile BEACH = new TETile(' ',Color.WHITE,new Color(175, 209, 62),
            "Grass");
    public static final TETile PLAINS = new TETile(' ',Color.WHITE,new Color(113, 174, 78),
            "Grass");
    public static final TETile FOREST = new TETile(' ',Color.WHITE,new Color(113, 134, 78),
            "Grass");
    public static final TETile DEEP_FOREST = new TETile(' ',Color.WHITE,new Color(62, 102, 23),
            "Grass");
    public static final TETile HILLS = new TETile(' ',Color.WHITE,new Color(166, 140, 105),
            "Grass");
    public static final TETile CLIFFS = new TETile(' ',Color.WHITE,new Color(168, 149, 143),
            "Grass");
    public static final TETile MOUNTAINS = new TETile(' ',Color.WHITE,new Color(150, 129, 122),
            "Grass");
    public static final TETile HIGH_MOUNTAINS = new TETile(' ',Color.WHITE,new Color(84, 106, 107),
            "Grass");
    public static final TETile ICY_MOUNTAINS = new TETile(' ',Color.WHITE,new Color(44, 97, 89),
            "Grass");
    public static final TETile ICE = new TETile(' ',Color.WHITE,Color.white,
            "Grass");
    public static final TETile GRASS = new TETile(' ',Color.WHITE,new Color(43, 117, 66),"Grass");
    public static final TETile SAND = new TETile('▒', Color.yellow, baseColor, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, baseColor, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, baseColor, "tree");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");

    //Item tiles
    public static final TETile KEY = new TETile('‡', Color.YELLOW, baseColor, "Key");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, baseColor,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, baseColor,
            "unlocked door");
    public static final TETile LIGHT = new TETile('O', Color.WHITE, baseColor,
            "Lamp");
    public static final TETile TROPHY = new TETile('þ', Color.YELLOW, baseColor,
            "Trophy");
}


