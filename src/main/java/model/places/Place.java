package model.places;

import model.categories.Category;

public class Place {
    private String name;
    private Category category;

    public Place(String name, Category category) {
        this.name = name;
        this.category = category;
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

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", category=" + category +
                '}';
    }
}
