package core;

import exceptions2.HumanHeightException;

import java.io.Serializable;

public class Human implements Serializable {
    private Double height;

    public Human(Double height){
        setHeight(height);
    }

    public void setHeight(Double height){
        if(height <= 0){
            throw new HumanHeightException("Значение поля height должно быть больше 0");
        }
        this.height = height;
    }

    public Double getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "Human{height=" + height + "}";
    }
}
