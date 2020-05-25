package model;

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
    private Map<Position, Place> places;
    private Set<Place> marked;
    private Set<Place> hidden;
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
                    entry -> place.getName().
                            equalsIgnoreCase(entry.getValue().getName())
            );
        }
        marked.clear();
    }

    // Looks for a place at the given coordinates
    public String placeByCoordinates(int x, int y) {
        if (places.containsKey(new Position(x, y))) {
            Place place = places.get(new Position(x, y));
            marked.add(place);
            return place.getName();
        }
        return null;
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

    public void printPlaces() {
        for (Map.Entry<Position, Place> entry : places.entrySet()) {
            System.out.println(
                    "Position " + entry.getKey().x + ", " + entry.getKey().y +
                            "|| Place " + entry.getValue().getName() + ", " + entry.getValue().getCategory().getName()
            );
        }
    }

    public void printMarked() {
        for (Place place : marked) {
            System.out.println("Name: " + place.getName() +
                    ", Category: " + place.getCategory().getName());
        }
    }

    public void printHidden() {
        for (Place place : hidden) {
            System.out.println("Name: " + place.getName() +
                    ", Category: " + place.getCategory().getName());
        }
    }

    public Map<Position, Place> getPlaces() {
        return places;
    }

    public void setPlaces(Map<Position, Place> places) {
        this.places = places;
    }

    public Set<Place> getMarked() {
        return marked;
    }

    public void setMarked(Set<Place> marked) {
        this.marked = marked;
    }

    public Set<Place> getHidden() {
        return hidden;
    }

    public void setHidden(Set<Place> hidden) {
        this.hidden = hidden;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
