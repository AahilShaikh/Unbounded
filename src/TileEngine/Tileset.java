package TileEngine;

import java.awt.*;
import java.util.ArrayList;

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
    public static final TETile MONSTER = new TETile('M', Color.RED, baseColor, "Monster");
    public static final TETile ATTACKED_MONSTER = new TETile('M', Color.WHITE, baseColor,
            "Monster");

    //Dungeon tiles
    public static final TETile FLOOR = new TETile('·', Color.WHITE, baseColor, "Floor");
    public static final TETile WALL = new TETile('|', wallColor, wallColor,
            "Wall");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "Nothing");

    //Outside tiles
    public static final TETile DEEP_OCEAN = new TETile(' ',Color.WHITE,new Color(1, 38, 119),
            "Deep Ocean");
    public static final TETile OCEAN = new TETile(' ',Color.WHITE,new Color(0, 91, 197),
            "Ocean");
    public static final TETile SEA = new TETile(' ',Color.WHITE,new Color(0, 180, 252),
            "Sea");
    public static final TETile BEACH = new TETile(' ',Color.WHITE,new Color(224, 215, 114),
            "Beach");
    public static final TETile PLAINS = new TETile(' ',Color.WHITE,new Color(43, 117, 66),
            "Plains");
    public static final TETile FOREST = new TETile(' ',Color.WHITE,new Color(8, 112, 41),
            "Forest");
    public static final TETile DEEP_FOREST = new TETile(' ',Color.WHITE,new Color(3, 79, 26),
            "Deep Forest");
    public static final TETile HILLS = new TETile(' ',Color.WHITE,new Color(166, 140, 105),
            "Hills");
    public static final TETile CLIFFS = new TETile(' ',Color.WHITE,new Color(168, 149, 143),
            "Cliffs");
    public static final TETile MOUNTAINS = new TETile(' ',Color.WHITE,new Color(150, 129, 122),
            "Mountains");
    public static final TETile HIGH_MOUNTAINS = new TETile(' ',Color.WHITE,new Color(84, 106, 107),
            "High Mountains");
    public static final TETile ICY_MOUNTAINS = new TETile(' ',Color.WHITE,new Color(44, 97, 89),
            "Icy Mountains");
    public static final TETile ICE = new TETile(' ',Color.WHITE,Color.white,
            "Ice");
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
    public static final TETile TEST_SQUARE = new TETile(' ', Color.WHITE, baseColor, "Test tile" );

    public static ArrayList<TETile> reachableEntityTiles = new ArrayList<>() {{
        add(FLOOR);
        add(PLAINS);
        add(BEACH);
        add(UNLOCKED_DOOR);
        add(TREE);
        add(AVATAR);
        add(FLOWER);
    }};

    public static ArrayList<TETile> reachableAttackTiles = new ArrayList<>() {{
        add(FLOOR);
        add(PLAINS);
        add(BEACH);
        add(UNLOCKED_DOOR);
        add(TREE);
        add(AVATAR);
        add(FLOWER);
        add(DEEP_OCEAN);
        add(OCEAN);
        add(SEA);
    }};




}


