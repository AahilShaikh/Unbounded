package Core.Input;

public class StringInputSource implements InputSource {
    String input;
    int index = -1;

    public StringInputSource(String input) {
        this.input = input;
    }

    @Override
    public char getNextKey() {
        index++;
        try {
            return Character.toLowerCase(input.charAt(index));
        } catch (StringIndexOutOfBoundsException e) {
            return Character.MIN_VALUE;
        }
    }

    public char peekNextKey() {
        try {
            return Character.toLowerCase(input.charAt(index + 1));
        } catch (StringIndexOutOfBoundsException e) {
            return Character.MIN_VALUE;
        }
    }
}
