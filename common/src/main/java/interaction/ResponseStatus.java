package interaction;

import java.io.Serializable;

public enum ResponseStatus implements Serializable {
    SUCCESS,
    CLIENT_EXIT,
    ERROR,
    SERVER_SHUTDOWN,
    READ_ELEM,
    EXECUTE_SCRIPT
}
