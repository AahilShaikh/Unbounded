package Core;

import Core.DataStructures.GameStatus;
import Core.DataStructures.Tile;
import Core.Entities.Lamp;
import Core.Entities.Monster;
import Core.Input.InputSource;
import Core.Input.KeyboardInputSource;
import Core.Input.StringInputSource;
import TileEngine.TERenderer;
import TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Manages the various states that the game is in.
 * Main menu, enter seed screen, actual game, game over screen, and win screen.
 */
public class GameStateManager implements Serializable {
    private InputSource userInput;
    
    private WorldEngine worldEngine;
    
    public GameStateManager(InputSource userInput) {
        this.userInput = userInput;
        this.worldEngine = new WorldEngine(-1, Constants.STAGE_WIDTH, Constants.STAGE_HEIGHT,
                Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        GameServices.getInstance().initialize(0, GameStatus.IN_PROGRESS);
    }
    public void startNewGame() {
        if ((userInput instanceof StringInputSource)
                && ((StringInputSource) userInput).peekNextKey() == 'l') {
            Save.loadGame(Constants.SAVE_FILE, this);
            startUserInput();
            return;
        }
        worldEngine.createDungeon(100, Tileset.NOTHING.copyOf(),
                Tileset.FLOOR.copyOf(), Tileset.WALL.copyOf());
//        worldEngine.createOutside(Tileset.TREE, Tileset.TEST_SQUARE, Tileset.MOUNTAIN);
        worldEngine.createPlayer();
        TERenderer.getInstance().renderFrame(worldEngine.getCurrentChunk().getMap(),
                GameServices.getInstance().getPlayer().currentLocation(), GameServices.getInstance().getPlayer(),
                worldEngine.getCurrentChunk().getMobs());
        //get user input
        startUserInput();
    }

    public void startUserInput() {
        ArrayList<Lamp> lamps = worldEngine.getCurrentChunk().getLamps();
        while (GameServices.getInstance().getGameStatus() == GameStatus.IN_PROGRESS) {
            //slowly increase player health and mana
            if(GameServices.getInstance().getPlayerRegenCount() > 3) {
                GameServices.getInstance().getPlayer().increaseMana(5);
                GameServices.getInstance().getPlayer().increaseHealth(5);
                GameServices.getInstance().setPlayerRegenCount(0);
            } else {
                GameServices.getInstance().setPlayerRegenCount(GameServices.getInstance().getPlayerRegenCount()+1);
            }

            char c = userInput.getNextKey();
            //if there is no next key, then end the game - only pertains to interactWithInputString
            if (c == Character.MIN_VALUE) {
                return;
            }
            //Quit the game
            if (c == ':' && Character.toLowerCase(userInput.getNextKey()) == 'q') {
                new Save(this, GameServices.getInstance(), TERenderer.getInstance()).saveGame(Constants.SAVE_FILE);
                return;
            } else if (c == 'w' || c == 's' || c == 'a' || c == 'd') {
                //move the player
                GameServices.getInstance().getPlayer().move(c);
                //move the monsters
                if (userInput instanceof KeyboardInputSource) {
                    worldEngine.getCurrentChunk().getMobs().forEach(Monster::move);
                }
            } else if (c == 'n') {
                //interact with the nearest object
                GameServices.getInstance().getPlayer().interactWith(worldEngine.getCurrentChunk().getInteractables());
            } else if (c == 'm') {
                //make the player attack
                GameServices.getInstance().getPlayer().attack();
                //make the mobs next to the player attack
                worldEngine.getCurrentChunk().getMobs().forEach(Monster::attack);
            } else if (c == 'p') {
                //show the path from each mob to the player
                worldEngine.getCurrentChunk().getMobs().forEach(Monster::changeShowPath);
            } else if (c == 'l') {
                //turn all lamps off / on
                lamps.forEach(Lamp::action);
            }
            TERenderer.getInstance().renderFrame(worldEngine.getCurrentChunk().getFloorArray(),
                    GameServices.getInstance().getPlayer().currentLocation(),
                    GameServices.getInstance().getPlayer(),
                    worldEngine.getCurrentChunk().getMobs());
        }
        if(GameServices.getInstance().getGameStatus().equals(GameStatus.LOST)) {
            gameOverScreen();

        }else if(GameServices.getInstance().getGameStatus() == GameStatus.WON) {
            winScreen();
        }

    }

    /**
     * Shows the main menu. For use with keyboard input.
     */
    public void showMainMenu() {
        if (TERenderer.getInstance().isOn()) {
            float newWidth = (float)  TERenderer.getInstance().getViewportWidth() / 2;
            float newHeight = (float) TERenderer.getInstance().getViewportHeight() / 2;
            //Set the background to black
            StdDraw.clear(Color.BLACK);
            //animate the title into position
            Font fontBig;
            for (double y = 0; y < 10; y += 0.5) {
                fontBig = new Font("Monaco", Font.BOLD, (int) (20 + (y * 3)));
                StdDraw.setFont(fontBig);
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE.brighter());
                StdDraw.text(newWidth, newHeight + y, "Unbounded");
                StdDraw.pause(50);
                StdDraw.show();
            }
            StdDraw.setPenColor(Color.WHITE);


            //animate the menu options into position
            for (float i = 0; i < 1; i += 0.05) {
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(new Color(1f, 1f, 1f, i));
                fontBig = new Font("Monaco", Font.BOLD, 80);
                StdDraw.setFont(fontBig);
                StdDraw.text(newWidth, newHeight + 10, "Unbounded");
                fontBig = new Font("Monaco", Font.BOLD, 30);
                StdDraw.setFont(fontBig);
                StdDraw.text(newWidth, newHeight, "New Game (N)");
                StdDraw.text(newWidth, newHeight - 5, "Load Game (L)");
                StdDraw.text(newWidth, newHeight - 10, "Quit Game (Q)");
                StdDraw.pause(50);
                StdDraw.show();
            }
            while (true) {
                char input = userInput.getNextKey();
                if (input == 'n') {
                    enterSeedScreen();
                    return;
                } else if (input == 'l') {
                    Save.loadGame(Constants.SAVE_FILE, this);
                    startUserInput();
                    return;
                } else if (input == 'q') {
                    return;
                }
            }
        }
    }

    /**
     * Called by the show main menu method. Allows the user to input a seed for the game. The
     * user can indicate that their finished inputting the seed by pressing s.
     */
    private void enterSeedScreen() {
        if (TERenderer.getInstance().isOn()) {
            float newWidth = (float) TERenderer.getInstance().getViewportWidth() / 2;
            float newHeight = (float) TERenderer.getInstance().getViewportHeight() / 2;
            StdDraw.setPenColor(Color.WHITE);
            Font fontBig = new Font("Monaco", Font.BOLD, 80);
            StdDraw.setFont(fontBig);
            StdDraw.clear(Color.BLACK);
            StdDraw.text(newWidth, newHeight + 10, "Enter a Seed:");
            StdDraw.show();
            String input = "";
            while (true) {
                StdDraw.clear(Color.BLACK);
                StdDraw.text(newWidth, newHeight + 10, "Enter a Seed:");
                char c = userInput.getNextKey();
                if (c == 's') {
                    worldEngine.setWorldEngineRng(Long.parseLong(input));
                    startNewGame();
                    return;
                }
                input += c;
                StdDraw.text(newWidth, newHeight, input);
                StdDraw.show();
            }
        }
    }

    private void gameOverScreen() {
        Constants.EXECUTOR_SERVICE.shutdown();
        try {
            Constants.EXECUTOR_SERVICE.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }

        if (TERenderer.getInstance().isOn()) {
            StdDraw.clear(Color.BLACK);
            StdDraw.clear(Color.BLACK);
            StdDraw.clear(Color.BLACK);
            StdDraw.show();
            float newWidth = (float) TERenderer.getInstance().getViewportWidth() / 2;
            float newHeight = (float) TERenderer.getInstance().getViewportHeight() / 2;
            StdDraw.setPenColor(Color.WHITE);
            Font font = new Font("Monaco", Font.BOLD, 60);
            StdDraw.setFont(font);
            StdDraw.text(newWidth, newHeight + 10, "Game Over: You Lost");
            font = new Font("Monaco", Font.BOLD, 40);
            StdDraw.setFont(font);
            StdDraw.text(newWidth, newHeight + 5, "Press Q to quit");
            StdDraw.show();
            while (true) {
                font = new Font("Monaco", Font.BOLD, 60);
                StdDraw.setFont(font);
                StdDraw.text(newWidth, newHeight + 10, "Game Over: You Lost");
                font = new Font("Monaco", Font.BOLD, 40);
                StdDraw.setFont(font);
                StdDraw.text(newWidth, newHeight + 5, "Press Q to quit");
                StdDraw.show();
                char c = userInput.getNextKey();
                if (c == 'q') {
                    return;
                }
            }

        }
    }

    private void winScreen() {
        Constants.EXECUTOR_SERVICE.shutdown();
        try {
            Constants.EXECUTOR_SERVICE.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }
        if (TERenderer.getInstance().isOn()) {
            TERenderer.setInstance(null);
            StdDraw.clear(Color.BLACK);
            float newWidth = (float) TERenderer.getInstance().getViewportWidth() / 2;
            float newHeight = (float) TERenderer.getInstance().getViewportHeight() / 2;
            StdDraw.setPenColor(Color.WHITE);
            Font fontBig = new Font("Monaco", Font.BOLD, 80);
            StdDraw.setFont(fontBig);
            StdDraw.clear(Color.BLACK);
            StdDraw.text(newWidth, newHeight + 10, "You Win!");
            fontBig = new Font("Monaco", Font.BOLD, 30);
            StdDraw.setFont(fontBig);
            StdDraw.text(newWidth, newHeight + 5, "Press Q to quit");
            StdDraw.show();
            while (true) {
                StdDraw.clear(Color.BLACK);
                StdDraw.text(newWidth, newHeight + 10, "You Win!");
                fontBig = new Font("Monaco", Font.PLAIN, 30);
                StdDraw.setFont(fontBig);
                StdDraw.text(newWidth, newHeight + 5, "Press Q to quit");
                StdDraw.show();
                char c = userInput.getNextKey();
                if (c == 'q') {
                    return;
                }
            }

        }
    }

    public WorldEngine getWorldEngine() {
        return worldEngine;
    }

    public InputSource getUserInput() {
        return userInput;
    }

    public void setUserInput(InputSource userInput) {
        this.userInput = userInput;
    }

    public void setWorldEngine(WorldEngine worldEngine) {
        this.worldEngine = worldEngine;
    }
}
