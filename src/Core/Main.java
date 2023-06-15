package Core;

import Core.Input.KeyboardInputSource;
import Core.Input.StringInputSource;
import TileEngine.TERenderer;
import TileEngine.TETile;

import java.util.concurrent.TimeUnit;

/**
 * This is the main entry point for the program. This class simply parses
 * the command line inputs, and lets the byow.Core.Engine class take over
 * in either keyboard or input string mode.
 */
public class Main {
    public static void main(String[] args) {
        interactWithKeyboard();
        Constants.EXECUTOR_SERVICE.shutdown();
        try {
            Constants.EXECUTOR_SERVICE.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new UnsupportedOperationException();
        }
        System.exit(0);
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public static void interactWithKeyboard() {
        GameStateManager game = new GameStateManager(new KeyboardInputSource());
        game.showMainMenu();
    }

    /**
     * Method for playing the game through the commandline. Pass in a string with
     * the keyboard inputs and the game will be played without a UI.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] interactWithInputString(String input) {
        input = input.toLowerCase();
        int start = input.indexOf("n");
        int stop = input.indexOf("s");
        String newInput;
        long seed;
        if (start == -1) {
            newInput = input;
            seed = -1;
        } else {
            seed = Long.parseLong(input.substring(start + 1, stop));
            newInput = input.substring(stop + 1);
        }
        GameStateManager game = new GameStateManager(new StringInputSource(newInput));
        TERenderer.getInstance().setOn(false);
        game.startNewDungeonGame();
        return game.getWorldEngine().getCurrentChunk().getFloorArray();
    }
}
