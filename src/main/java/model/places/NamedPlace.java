package model.places;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import model.Position;
import model.categories.Category;

public class NamedPlace extends Polygon {
    private String name;
    private Category category;
    private Position position;

    public NamedPlace(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public void draw(Color color) {
        int x = position.x;
        int y = position.y;
        this.setManaged(false);
        this.getPoints().addAll(
                (double) x, (double) y,
                x - 7.0, y - 10.0,
                x + 7.0, y - 10.0
        );
        this.setFill(color);
        this.setStroke(Color.BLACK);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", category=" + category +
                '}';
    }
}
