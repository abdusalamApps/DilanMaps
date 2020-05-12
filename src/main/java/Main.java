import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Main  extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox mainContainer = new VBox();

        mainContainer.getChildren().addAll(getTopContainer(), getBottomContainer());

        Scene scene = new Scene(mainContainer, 1100, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox getTopContainer() {
        HBox topContainer = new HBox();
        topContainer.setPadding(new Insets(14, 16, 14, 16));
        topContainer.setSpacing(12);

        Button newButton = new Button("New"); // Skapa 5 stycken
        Button searchButton = new Button("Search");
        Button removeButton = new Button("Remove");
        Button hideButton = new Button("Hide");
        Button coordinatesButton = new Button("Coordinates");

        TextField textField = new TextField();
        topContainer.getChildren().addAll(newButton, getRadioBox(), textField,
                searchButton, removeButton, hideButton, coordinatesButton);

        return topContainer;
    }

    private VBox getRadioBox() {
        RadioButton namedRadio = new RadioButton("Named"); // Skapa 2 av den här och lägga dem i vbox
        RadioButton described = new RadioButton("Described"); // Skapa 2 av den här och lägga dem i vbox
        VBox radioBox = new VBox();
        radioBox.getChildren().addAll(namedRadio, described);
        radioBox.setSpacing(6);
        ToggleGroup toggleGroup = new ToggleGroup(); // Lägg radio buttons i den här
        namedRadio.setToggleGroup(toggleGroup);
        described.setToggleGroup(toggleGroup);

        return radioBox;
    }

    private HBox getBottomContainer() {
        HBox bottomContainer = new HBox();
        StackPane mapStack = new StackPane();
        ImageView imageView = new ImageView();

        try {
            InputStream inputStream = new FileInputStream("C:/AwesomeMaps/exempelkarta.png");
            imageView.setImage(new Image(inputStream));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        mapStack.getChildren().addAll(imageView);
        bottomContainer.getChildren().addAll(mapStack, getRightPanel());
        return bottomContainer;
    }

    private VBox getRightPanel() {
        VBox rightPanel = new VBox();
        Label categoriesLabel = new Label("Categories");

        ListView<String> categoriesListView = new ListView<>();
        List<String> categories = new ArrayList<>();
        categories.add("Train");
        categories.add("Bus");
        categories.add("Underground");
        categoriesListView.setItems(FXCollections.observableList(categories));

        Button hideCatButton = new Button("Hide Categories");
        rightPanel.setSpacing(12);
        rightPanel.setPadding(new Insets(14, 16, 14, 16));

        rightPanel.getChildren().addAll(categoriesLabel, categoriesListView, hideCatButton);

        return rightPanel;
    }
}
