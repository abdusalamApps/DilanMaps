package model;

import javafx.geometry.Pos;
import model.categories.BusCategory;
import model.categories.Category;
import model.categories.TrainCategory;
import model.categories.UndergroundCategory;
import model.places.DescribedPlace;
import model.places.Place;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Data {
    Map<Position, Place> places;
    Set<Place> marked;
    Set<Place> hidden;
    boolean changed;

    public Data() {
        places = new HashMap<>();
        marked = new HashSet<>();
        hidden = new HashSet<>();
        changed = false;
    }

    // Search after a place by its name
    public void search(String place) {
        for (Map.Entry<Position, Place> entry : places.entrySet()) {
            if (place.equalsIgnoreCase(entry.getValue().getName())) {
                marked.add(entry.getValue());
            }
        }
    }

    // Hides all marked places
    public void hide() {
        hidden.addAll(marked);
        marked.clear();
    }

    // Hide all places of a certain category
    public void hideCategory(String category) {
        for (Place place : places.values()) {
            if (category.equalsIgnoreCase(place.getCategory().getName())) {
                hidden.add(place);
                marked.remove(place);
            }
        }
    }

    // Show hidden category
    public void showCategory(String category) {
        for (Place place : places.values()) {
            if (category.equalsIgnoreCase(place.getCategory().getName())) {
                hidden.remove(place);
            }
        }
    }

    // Removes all marked places
    public void remove() {
        for (Place place : marked) {
            places.entrySet().removeIf(
                    entry -> marked.contains(entry.getValue())
            );
        }
    }

    // Looks for a place at the given coordinates
    public void placeByCoordinates(int x, int y) {
        if (places.containsKey(new Position(x, y))) {
            marked.add(places.get(new Position(x, y)));
        }
    }

    public void add(int x, int y, String name, String categoryName) {
        places.put(
                new Position(x, y),
                new Place(name, makeCategory(categoryName))
        );
    }

    public void add(int x, int y, String name, String description, String categoryName) {
        places.put(
                new Position(x, y),
                new DescribedPlace(name, makeCategory(categoryName), description)
        );
    }

    private Category makeCategory(String categoryName) {
        if (categoryName.isEmpty() || categoryName == null) {
            return new Category();
        } else if (categoryName.equalsIgnoreCase("Bus")) {
            return new BusCategory();
        } else if (categoryName.equalsIgnoreCase("Train")) {
            return new TrainCategory();
        } else if (categoryName.equalsIgnoreCase("Underground")) {
            return new UndergroundCategory();
        } else {
            return new Category();
        }
    }

    public void mark(String name) {
        for (Place place : places.values()) {
            if (name.equalsIgnoreCase(place.getName())) {
                marked.add(place);
            }
        }
    }
}
