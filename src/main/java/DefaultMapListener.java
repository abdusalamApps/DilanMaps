import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.Data;
import model.Position;
import model.places.DescribedPlace;
import model.places.Place;

import java.util.Map;

public class DefaultMapListener implements EventHandler<MouseEvent> {

    private Data data;
    private Main gui;

    public DefaultMapListener(Data data, Main gui) {
        this.data = data;
        this.gui = gui;
    }

    @Override
    public void handle(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            for (Map.Entry<Position, Place> entry : data.getPlaces().entrySet()) {
                int x = entry.getKey().x;
                int y = entry.getKey().y;
                if (Math.abs(x - e.getX()) < 7 && Math.abs(y - e.getY()) < 7) {
                    if (data.isMarked(x, y)) {
                        data.unMark(x, y);
                    } else {
                        data.mark(x, y);
                    }
                    gui.refreshMap(data.getPlaces());
                }
            }
        } else if (e.getButton() == MouseButton.SECONDARY) {
            for (Map.Entry<Position, Place> entry : data.getPlaces().entrySet()) {
                int x = entry.getKey().x;
                int y = entry.getKey().y;
                if (Math.abs(x - e.getX()) < 7 && Math.abs(y - e.getY()) < 7) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    Place place = entry.getValue();
                    if (place.getClass().equals(DescribedPlace.class)) {
                        DescribedPlace describedPlace = (DescribedPlace) place;
                        alert.setContentText("Name: " + place.getName()
                                + " [" + x + ", " + y + "]\n" +
                                "Description: " + describedPlace.getDescription());
                    } else {
                        alert.setContentText("Name: " + place.getName()
                                + " [" + x + ", " + y + "]");
                    }
                    alert.show();
                }
            }

        }
    }
}
