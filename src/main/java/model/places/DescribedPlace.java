package model.places;

import model.categories.Category;

public class DescribedPlace extends NamedPlace {

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
