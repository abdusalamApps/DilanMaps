package model.categories;

import javafx.scene.paint.Color;

public class Category {

    protected String name;
    protected Color color;

    public Category() {
        this.name = "Blank";
        this.color = Color.BLACK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
