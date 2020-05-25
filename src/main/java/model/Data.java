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
    private Map<Position, Place> marked;
    private Map<Position, Place> hidden;
    boolean changed;

    public Data() {
        places = new HashMap<>();
        marked = new HashMap<>();
        hidden = new HashMap<>();
        changed = false;
    }

    // Search after a place by its name
    public void search(String place) {
        for (Map.Entry<Position, Place> entry : places.entrySet()) {
            if (place.equalsIgnoreCase(entry.getValue().getName())) {
                marked.put(entry.getKey(), entry.getValue());
            }
        }
    }

    // Hides all marked places
    public void hide() {
        for (Map.Entry<Position, Place> entry : marked.entrySet()) {
            hidden.put(entry.getKey(), entry.getValue());
        }
        marked.clear();
    }

    // Hide all places of a certain category
    public void hideCategory(String category) {
        for (Map.Entry<Position, Place> entry : places.entrySet()) {
            if (category.equalsIgnoreCase(entry.getValue().getCategory().getName())) {
                hidden.put(entry.getKey(), entry.getValue());
                marked.remove(entry.getKey());
            }
        }
    }

    // Show hidden category
    public void showCategory(String category) {
        for (Map.Entry<Position, Place> entry : places.entrySet()) {
            if (category.equalsIgnoreCase(entry.getValue().getCategory().getName())) {
                hidden.remove(entry.getKey());
            }
        }
    }

    // Removes all marked places
    public void remove() {
        for (Map.Entry<Position, Place> entry : marked.entrySet()) {
            places.entrySet().removeIf(
                    e -> e.getKey().x == entry.getKey().x
                    && e.getKey().y == entry.getKey().y
            );
        }
        marked.clear();
    }

    // Looks for a place at the given coordinates
    public String placeByCoordinates(int x, int y) {
        if (places.containsKey(new Position(x, y))) {
            Place place = places.get(new Position(x, y));
            marked.put(new Position(x, y), place);
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

    public void mark(int x, int y) {
        for (Map.Entry<Position, Place> entry : places.entrySet()) {
            if (entry.getKey().x == x && entry.getKey().y == y) {
                marked.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public boolean isMarked(int x, int y) {
        return marked.containsKey(new Position(x, y));
    }

    public boolean isMarked(double x, double y) {
        return marked.containsKey(new Position((int) x, (int) y));
    }

    public boolean isHidden(int x, int y) {
        return hidden.containsKey(new Position(x, y));
    }

    public boolean isHidden(double x, double y) {
        return hidden.containsKey(new Position((int) x, (int) y));
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
        for (Place place : marked.values()) {
            System.out.println("Name: " + place.getName() +
                    ", Category: " + place.getCategory().getName());
        }
    }

    public void printHidden() {
        for (Place place : hidden.values()) {
            System.out.println("Name: " + place.getName() +
                    ", Category: " + place.getCategory().getName());
        }
    }

    public void clear() {
        places.clear();
        marked.clear();
        hidden.clear();
    }

    public Map<Position, Place> getPlaces() {
        return places;
    }

    public void setPlaces(Map<Position, Place> places) {
        this.places = places;
    }

    public Map<Position, Place> getMarked() {
        return marked;
    }

    public void setMarked(Map<Position, Place> marked) {
        this.marked = marked;
    }

    public Map<Position, Place> getHidden() {
        return hidden;
    }

    public void setHidden(Map<Position, Place> hidden) {
        this.hidden = hidden;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void unMark(int x, int y) {
        marked.remove(new Position(x, y));
    }
}
