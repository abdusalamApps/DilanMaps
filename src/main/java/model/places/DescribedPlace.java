package model.places;

import model.categories.Category;
import model.places.Place;

public class DescribedPlace extends Place {

    private String description;

    public DescribedPlace(String name, Category category, String description) {
        super(name, category);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
