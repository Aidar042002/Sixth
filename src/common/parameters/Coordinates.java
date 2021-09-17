package common.parameters;

import java.io.Serializable;

/*
    Coordinates class
 */
public class Coordinates implements Serializable {
    private Long x; //Поле не может быть null
    private long y;

    public Long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public void setY(long y) {
        this.y = y;
    }
}
