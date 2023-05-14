package Core;

import Core.DataStructures.GameStatus;
import Core.Entities.Player;

import java.io.Serializable;

public class GameServices implements Serializable {
    private Player player;

    private int playerRegenCount;
    private GameStatus gameStatus;

    private static GameServices instance;
    private GameServices() {}

    public static GameServices getInstance() {
        if(instance == null) {
            instance = new GameServices();
        }
        return instance;
    }

    public static void setInstance(GameServices gameServices) {
        instance = gameServices;
    }

    public void initialize(int regenCount, GameStatus status) {
        this.playerRegenCount = regenCount;
        gameStatus = status;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPlayerRegenCount() {
        return playerRegenCount;
    }

    public void setPlayerRegenCount(int playerRegenCount) {
        this.playerRegenCount = playerRegenCount;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }
}
