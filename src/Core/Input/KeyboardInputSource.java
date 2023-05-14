package Core.Input;

import edu.princeton.cs.algs4.StdDraw;

public class KeyboardInputSource implements InputSource {
    @Override
    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                return Character.toLowerCase(StdDraw.nextKeyTyped());
            }
        }
    }
}
