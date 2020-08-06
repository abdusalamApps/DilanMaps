import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import model.Data;
import model.Position;
import model.places.DescribedPlace;
import model.places.NamedPlace;

import java.util.Map;

public class PlaceClickListener implements EventHandler<MouseEvent> {

    private Data data;
    private NamedPlace place;

    public PlaceClickListener(Data data, NamedPlace place) {
        this.data = data;
        this.place = place;
    }

    @Override
    public void handle(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            if (!data.isMarked(place)) {
                System.out.println("-----------Mark mouse click----------");
                data.mark(place);
                place.setFill(Color.YELLOW);
                System.out.println("Marked size = " + data.getMarked().size());
            } else {
                System.out.println("-----------Unmark mouse click------------");
                data.unMark(place);
                place.setFill(place.getCategory().getColor());
                System.out.println("Marked size = " + data.getMarked().size());
            }
        } else if (e.getButton() == MouseButton.SECONDARY) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Name :").append(place.getName())
                    .append("[").append(place.getPosition().x)
                    .append(",").append(place.getPosition().y).append("]")
                    .append("\n")
                    .append("Category: ").append(place.getCategory().getName())
                    .append("\n");
            if (place.getClass().equals(DescribedPlace.class)) {
                stringBuilder.append("Description: ").append(((DescribedPlace) place).getDescription());
            }
            alert.setContentText(stringBuilder.toString());
            alert.show();

        }
    }
}
