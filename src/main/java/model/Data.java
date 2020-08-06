package model;

import model.categories.BusCategory;
import model.categories.Category;
import model.categories.TrainCategory;
import model.categories.UndergroundCategory;
import model.places.DescribedPlace;
import model.places.NamedPlace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Data {
    private Map<Position, NamedPlace> positionsPlaces;
    private Map<String, Set<NamedPlace>> namesPlaces;
    private Map<String, Set<NamedPlace>> categoriesPlaces;
    private Set<NamedPlace> marked;
    private Set<NamedPlace> hidden;
    private boolean changed;

    private NamedPlace lastAddedPlace;

    public Data() {
        positionsPlaces = new HashMap<>();
        namesPlaces = new HashMap<>();
        categoriesPlaces = new HashMap<>();
        marked = new HashSet<>();
        hidden = new HashSet<>();
        changed = false;
    }

    // Search after a place by its name
    public Set<NamedPlace> search(String place) {
        if (namesPlaces.containsKey(place)) {
            marked.clear();
            Set<NamedPlace> places = namesPlaces.get(place);
            hidden.removeAll(places);
            marked.addAll(places);
            System.out.println("Marked size after search = " + marked.size());
            return places;
        }
        return null;
    }

    // Hides all marked places
    public Set<NamedPlace> hide() {
        hidden.addAll(marked);
        marked.clear();
        return hidden;
    }

    // Hide all places of a certain category
    public Set<NamedPlace> hideCategory(String category) {
        if (categoriesPlaces.containsKey(category)) {
            Set<NamedPlace> places = categoriesPlaces.get(category);
            hidden.addAll(places);
            marked.removeAll(places);
            return places;
        }
        return null;
    }

    // Show hidden category
    public Set<NamedPlace> showCategory(String category) {
        if (categoriesPlaces.containsKey(category)) {
            Set<NamedPlace> places = categoriesPlaces.get(category);
            hidden.removeAll(places);
            return places;
        }
        return null;
    }

    // Removes all marked places
    public Set<NamedPlace> remove() {
        Set<NamedPlace> temp = new HashSet<>(marked);
        if (!marked.isEmpty()) {
            for (NamedPlace place : marked) {
                positionsPlaces.remove(place.getPosition());
                namesPlaces.get(place.getName()).remove(place);
                categoriesPlaces.get(place.getCategory().getName()).remove(place);
            }
            marked.clear();
            changed = true;
            return temp;
        }
        return null;
    }

    // Looks for a place at the given coordinates
    public NamedPlace placeByCoordinates(int x, int y) {
        if (positionsPlaces.containsKey(new Position(x, y))) {
            marked.clear();
            NamedPlace place = positionsPlaces.get(new Position(x, y));
            marked.add(place);
            System.out.println("-------PlaceByCoordinates-----------");
            System.out.println("Marked size = " + marked.size());
            return place;
        }
        return null;
    }

    public void add(boolean fromFile, String categoryName, int x, int y, String name, String description) {
        NamedPlace newPlace;
        Position position = new Position(x, y);
        if (description.equals("")) {
            newPlace = new NamedPlace(name, makeCategory(categoryName));
        } else {
            newPlace = new DescribedPlace(name, makeCategory(categoryName), description);
        }
        newPlace.setPosition(position);
        positionsPlaces.put(position, newPlace);
        if (namesPlaces.containsKey(name)) {
            namesPlaces.get(name).add(newPlace);
        } else {
            HashSet<NamedPlace> places = new HashSet<>();
            places.add(newPlace);
            namesPlaces.put(name, places);
        }
        if (categoryName.equals("") || categoryName == null)
            categoryName = "None";
        if (categoriesPlaces.containsKey(categoryName)) {
            categoriesPlaces.get(categoryName).add(newPlace);
        } else {
            HashSet<NamedPlace> places = new HashSet<>();
            places.add(newPlace);
            categoriesPlaces.put(categoryName, places);
        }
        lastAddedPlace = newPlace;
        if (!fromFile)
            changed = true;
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

    public void mark(NamedPlace place) {
        marked.add(place);
    }


    public void unMark(NamedPlace place) {
        marked.remove(place);
    }

    public boolean isMarked(NamedPlace place) {
        return marked.contains(place);
    }


    public boolean isHidden(NamedPlace place) {
        return hidden.contains(place);
    }

    public void printPlaces() {
        for (Map.Entry<Position, NamedPlace> entry : positionsPlaces.entrySet()) {
            System.out.println(
                    "Position " + entry.getKey().x + ", " + entry.getKey().y +
                            "|| Place " + entry.getValue().getName() + ", " + entry.getValue().getCategory().getName()
            );
        }
    }

    public void printMarked() {
        for (NamedPlace place : marked) {
            System.out.println("Name: " + place.getName() +
                    ", Category: " + place.getCategory().getName());
        }
    }

    public void printHidden() {
        for (NamedPlace place : hidden) {
            System.out.println("Name: " + place.getName() +
                    ", Category: " + place.getCategory().getName());
        }
    }

    public void clear() {
        positionsPlaces.clear();
        namesPlaces.clear();
        categoriesPlaces.clear();
        marked.clear();
        hidden.clear();
    }

    public Map<Position, NamedPlace> getPositionsPlaces() {
        return positionsPlaces;
    }

    public void setPositionsPlaces(Map<Position, NamedPlace> positionsPlaces) {
        this.positionsPlaces = positionsPlaces;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public NamedPlace getLastAddedPlace() {
        return this.lastAddedPlace;
    }

    public Set<NamedPlace> getMarked() {
        return this.marked;
    }
}
