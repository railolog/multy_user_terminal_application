package core;

import java.io.Serializable;

public enum Government implements Serializable {
    KLEPTOCRACY ("Клептократия"),
    NOOCRACY ("Ноократия"),
    OLIGARCHY ("Олигархия");

    private String title;

    Government(String title){
        this.title = title;
    }


    @Override
    public String toString() {
        return title;
    }
}
