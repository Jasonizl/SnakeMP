package util;

import javafx.scene.paint.Color;

/**
 * Created by Jason on 13.08.2017.
 */
public class Player {

    private int id;
    private String username;
    private Color color;

    public Player() {
        this.id = (int) (Math.random()*10000);
        this.username = "Player" + this.id;
        this.color = Color.color(Math.random(), Math.random(), Math.random(), 1);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
