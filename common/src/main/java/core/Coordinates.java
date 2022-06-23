package core;

import exceptions2.CoordinatesException;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private Float x;
    private Float y;

    public Coordinates(Float x, Float y){
        setX(x);
        setY(y);
    }

    public Coordinates(int x, int y) {
        setX(new Float(x));
        setY(new Float(y));
    }

    public void setX(Float x) throws CoordinatesException {
        if (x == null){
            throw new CoordinatesException("Значение не может быть null");
        }
        if (x > 98){
            throw new CoordinatesException("Некорректный ввод: максимальное значения поля x: 98");
        }
        this.x = x;
    }

    public void setY(Float y) throws CoordinatesException {
        if (x == null){
            throw new CoordinatesException("Значение не может быть null");
        }
        if (y > 761){
            throw new CoordinatesException("Некорректный ввод: максимальное значения поля y: 761");
        }
        this.y = y;
    }

    public Float getX() {
        return x;
    }

    public Float getY() {
        return y;
    }

    /**
     * Определяет четверть координатной плоскости
     * @return номер четверти
     */
    public int getQuarter(){
        if (x >= 0 && y >= 0){
            return 1;
        }
        if (x <= 0 && y >= 0){
            return 2;
        }
        if (x <= 0 && y <= 0){
            return 3;
        }
        return 4;
    }

    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }
}
