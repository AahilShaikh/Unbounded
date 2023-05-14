package Core.Input;

import java.io.Serializable;

public interface InputSource extends Serializable {
    char getNextKey();
}
