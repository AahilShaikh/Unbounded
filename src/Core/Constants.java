package Core;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Constants {
    public static final int VIEWPORT_WIDTH = 95;
    public static final int GUI_HEIGHT = 5;
    public static final int VIEWPORT_HEIGHT = 51;
    public static final int STAGE_WIDTH = 121;
    public static final int STAGE_HEIGHT = 121;

    /**
     * The place where game saves are saved.
     */
    public static final File SAVE_FILE = new File("save.txt");

    public static ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
}
