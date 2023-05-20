package Core;

import TileEngine.TERenderer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public record Save(GameStateManager gameManager, GameServices gameServices,
                   TERenderer renderer) implements Serializable {

    /**
     * Loads the game from the save file.
     *
     * @param file the save file to load the game from.
     */
    public static void loadGame(File file, GameStateManager manager) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Save load = (Save) in.readObject();
            //Services
            GameServices.setInstance(load.gameServices());
            TERenderer.setInstance(load.renderer());

            //World Engine variables
            manager.setWorldEngine(load.gameManager().getWorldEngine());
            manager.setUserInput(load.gameManager().getUserInput());

            TERenderer.getInstance().renderFrame(manager.getWorldEngine().getCurrentChunk().getFloorArray(),
                    GameServices.getInstance().getPlayer().getCurrentLocation(),
                    GameServices.getInstance().getPlayer(),
                    manager.getWorldEngine().getCurrentChunk().mobs());
        } catch (IOException | ClassCastException
                 | ClassNotFoundException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    /**
     * Saves the game to the specified file.
     *
     * @param file The file location where to save the game.
     */
    public void saveGame(File file) {
        writeContents(file, (Object) serialize());
    }

    private void writeContents(File file, Object... contents) {
        try (BufferedOutputStream str =
                     new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
            if (file.isDirectory()) {
                throw new IllegalArgumentException("cannot overwrite directory");
            }
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
        } catch (IOException | ClassCastException excp) {

            throw new IllegalArgumentException(excp);
        }
    }

    private byte[] serialize() {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(this);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp);
        }
    }
}
