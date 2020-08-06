import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Data;
import model.Position;
import model.places.DescribedPlace;
import model.places.NamedPlace;

import java.io.*;
import java.util.*;

public class Main extends Application {

    private Stage primaryStage;
    private Data data;
    private ImageView imageView;
    private StackPane mapPane;
    private Scene scene;
    private ToggleGroup toggleGroup;
    private ListView<String> categoriesListView;
    private String newPlaceCategory;
    private String description = "";

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.data = new Data();
        toggleGroup = new ToggleGroup();
        categoriesListView = new ListView<>();
        VBox mainContainer = new VBox();
        mapPane = new StackPane();
        newPlaceCategory = "";

        categoriesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            newPlaceCategory = newV;
        });

        mainContainer.getChildren().addAll(menuBar(), topContainer(), bottomContainer());
        scene = new Scene(mainContainer, 1100, 800);
        exitingBehavior(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void exitingBehavior(Stage stage) {
        stage.setOnCloseRequest(event -> {
            if (data.isChanged()) {
                Dialog<String> dialog = new Dialog<>();
                HBox hBox = new HBox();
                Label messageLabel = new Label("There are Unsaved Changes!");
                hBox.getChildren().addAll(messageLabel);

                ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                ButtonType exitType = new ButtonType("Exit", ButtonBar.ButtonData.OK_DONE);
                dialog.setResultConverter(buttonType -> {
                    if (buttonType.equals(exitType)) {
                        System.exit(0);
                    }
                    return null;
                });

                dialog.getDialogPane().getButtonTypes().addAll(cancelType, exitType);
                dialog.getDialogPane().setContent(hBox);
                dialog.show();

                event.consume();
            } else {
                System.exit(0);
            }

        });
    }

    private HBox topContainer() {
        HBox topContainer = new HBox();
        topContainer.setPadding(new Insets(14, 16, 14, 16));
        topContainer.setSpacing(12);
        Button newButton = new Button("New"); // Skapa 5 stycken
        Button searchButton = new Button("Search");
        Button removeButton = new Button("Remove");
        Button hideButton = new Button("Hide");
        Button coordinatesButton = new Button("Coordinates");
        TextField textField = new TextField();
        searchButton.setOnAction(e -> {
            Set<NamedPlace> prevMarked = new HashSet<>(data.getMarked());
            Set<NamedPlace> found = data.search(textField.getText().trim());
            if (found != null) {
                for (NamedPlace p : prevMarked) {
                    p.setFill(p.getCategory().getColor());
                }
                for (NamedPlace place : found) {
                    place.setFill(Color.YELLOW);
                    if (!mapPane.getChildren().contains(place)) {
                        mapPane.getChildren().add(place);
                    }

                }
            }
        });
        hideButton.setOnAction(e -> {
            Set<NamedPlace> hidden = data.hide();
            if (hidden != null)
                mapPane.getChildren().removeAll(hidden);
        });
        removeButton.setOnAction(e -> {
            Set<NamedPlace> removed = data.remove();
            if (removed != null)
                mapPane.getChildren().removeAll(removed);
        });
        coordinatesButton.setOnAction(e -> showCoordinatesDialog());
        newButton.setOnAction(e -> saveNewPlace());
        topContainer.getChildren().addAll(newButton, radioBox(), textField,
                searchButton, removeButton, hideButton, coordinatesButton);
        return topContainer;
    }

    private void showCoordinatesDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Input Coordinates");
        VBox mainBox = new VBox();
        mainBox.setSpacing(12);
        HBox xBox = new HBox();
        HBox yBox = new HBox();
        Label xLabel = new Label("X     ");
        TextField xField = new TextField();
        xBox.getChildren().addAll(xLabel, xField);
        Label yLabel = new Label("Y     ");
        TextField yField = new TextField();
        yBox.getChildren().addAll(yLabel, yField);
        ButtonType okType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.setResultConverter(buttonType -> {
            if (buttonType.equals(okType)) {
                int x = Integer.parseInt(xField.getText().trim());
                int y = Integer.parseInt(yField.getText().trim());
                Set<NamedPlace> prevMarked = new HashSet<>(data.getMarked());
                NamedPlace place = data.placeByCoordinates(x, y);
                if (place == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Place Not Found!");
                    alert.show();
                } else {
                    for (NamedPlace p : prevMarked) {
                        p.setFill(p.getCategory().getColor());
                    }
                    place.setFill(Color.YELLOW);
                }
            }
            return null;
        });
        mainBox.getChildren().addAll(xBox, yBox);
        dialog.getDialogPane().getButtonTypes().addAll(okType, cancelType);
        dialog.getDialogPane().setContent(mainBox);
        dialog.show();
    }

    private void saveNewPlace() {
        scene.setCursor(Cursor.CROSSHAIR);
        mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                int clickedX = (int) e.getX();
                int clickedY = (int) e.getY();
                if (data.placeByCoordinates(clickedX, clickedY) != null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Det är endast tillåtet med en plats per position!");
                    alert.show();
                } else {
                    RadioButton radioButton = (RadioButton) toggleGroup.getSelectedToggle();
                    Dialog<NamedPlace> dialog = new Dialog<>();
                    dialog.setTitle("Create New Place");
                    dialog.setResizable(true);
                    Label nameLabel = new Label("Name");
                    TextField nameField = new TextField();
                    VBox vBox = new VBox();
                    vBox.setSpacing(5);
                    vBox.getChildren().addAll(nameLabel, nameField);

                    Label descriptionLabel = new Label("Description");
                    TextField descriptionField = new TextField();

                    if (radioButton.getText().equalsIgnoreCase("described")) {
                        description = descriptionField.getText().trim();
                        vBox.getChildren().addAll(descriptionLabel, descriptionField);
                    }

                    ButtonType save = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
                    ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                    dialog.setResultConverter(button -> {
                        if (button.equals(save)) {
                            description = descriptionField.getText().trim();
                            String name = nameField.getText().trim();
                            data.add(false, newPlaceCategory, clickedX, clickedY, name, description);
                            NamedPlace newPlace = data.getLastAddedPlace();
                            newPlace.draw(newPlace.getCategory().getColor());
                            newPlace.setOnMouseClicked(new PlaceClickListener(data, newPlace));
                            mapPane.getChildren().add(newPlace);
                            mapPane.setOnMouseClicked(null);
                            scene.setCursor(Cursor.DEFAULT);
                        }
                        return null;
                    });

                    dialog.getDialogPane().setContent(vBox);
                    dialog.getDialogPane().getButtonTypes().addAll(save, cancel);
                    dialog.show();

                }
            }
        });
    }

    private void loadMap() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a Map");
        File file = chooser.showOpenDialog(primaryStage);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            imageView.setImage(new Image(inputStream));
            mapPane.getChildren().removeAll(data.getPositionsPlaces().values());
            data.clear();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void showLoadMapDialog() {
        if (data.isChanged()) {
            // Warn the user about unsaved changes
            Dialog<String> dialog = new Dialog<>();
            dialog.setContentText("There are unsaved changes!");
            ButtonType loadType = new ButtonType("Load Map Anyway", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.setResultConverter(buttonType -> {
                if (buttonType.equals(loadType)) {
                    // Load map anyway
                    loadMap();
                }
                return null;
            });
            dialog.getDialogPane().getButtonTypes().addAll(loadType, cancelType);
            dialog.show();
        } else {
            loadMap();
        }
    }

    private void showLoadPlacesDialog() {
        if (data.isChanged()) {
            // Warn the user about unsaved changes
            Dialog<String> dialog = new Dialog<>();
            dialog.setContentText("There are unsaved changes!");
            ButtonType loadType = new ButtonType("Load New Places Anyway", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.setResultConverter(buttonType -> {
                if (buttonType.equals(loadType)) {
                    // Load map anyway
                    loadPlaces();
                }
                return null;
            });
            dialog.getDialogPane().getButtonTypes().addAll(loadType, cancelType);
            dialog.show();
        } else {
            loadPlaces();
        }

    }

    private MenuBar menuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem loadMap = new MenuItem("Load Map");
        MenuItem loadPlaces = new MenuItem("Load Places");
        MenuItem save = new MenuItem("Save");
        MenuItem exit = new MenuItem("Exit");

        loadMap.setOnAction(e -> showLoadMapDialog());
        loadPlaces.setOnAction(e -> showLoadPlacesDialog());
        exit.setOnAction(e -> {
            if (data.isChanged()) {
                // Warn the user about unsaved changes
                Dialog<String> dialog = new Dialog<>();
                dialog.setContentText("There are unsaved changes!");
                ButtonType exitType = new ButtonType("Exit", ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.setResultConverter(buttonType -> {
                    if (buttonType.equals(exitType)) {
                        // Load map anyway
                        System.exit(0);
                    }
                    return null;
                });
                dialog.getDialogPane().getButtonTypes().addAll(exitType, cancelType);
                dialog.show();
            } else {
                System.exit(0);
            }
        });
        save.setOnAction(e -> savePlacesToFile());

        fileMenu.getItems().addAll(loadMap, loadPlaces, save, exit);
        menuBar.getMenus().add(fileMenu);
        return menuBar;
    }

    private void savePlacesToFile() {
        if (data.getPositionsPlaces().isEmpty() || !data.isChanged()) {
            // Save nothing
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("There are no unsaved changes...");
            alert.show();
        } else {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("(*.places)", "*.places");
            fileChooser.getExtensionFilters().add(extensionFilter);
            File file = fileChooser.showSaveDialog(primaryStage);
            try {
                PrintWriter writer = new PrintWriter(file);
                for (Map.Entry<Position, NamedPlace> entry : data.getPositionsPlaces().entrySet()) {
                    // Type,category,x,y,name,[description]
                    NamedPlace place = entry.getValue();
                    Position position = entry.getKey();

                    String type = "Named";
                    String category = place.getCategory().getName();
                    String x = String.valueOf(position.x);
                    String y = String.valueOf(position.y);
                    String name = place.getName();
                    if (place.getClass().equals(DescribedPlace.class)) {
                        type = "Described";
                        String description = ((DescribedPlace) place).getDescription();
                        writer.append(type).append(",").append(category).append(",")
                                .append(x).append(",").append(y).append(",")
                                .append(name).append(",").append(description).append("\n");
                    } else {
                        writer.append(type).append(",").append(category).append(",")
                                .append(x).append(",").append(y).append(",")
                                .append(name).append("\n");
                    }
                }
                writer.close();
                data.setChanged(false);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadPlaces() {
        mapPane.getChildren().removeAll(data.getPositionsPlaces().values());
        data.clear();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Places File");
        File file = chooser.showOpenDialog(primaryStage);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String[] line = scanner.nextLine().split(",");
                String type = line[0];
                String categoryName = line[1];
                int x = Integer.parseInt(line[2]);
                int y = Integer.parseInt(line[3]);
                String name = line[4];
                String description = "";
                if (type.equalsIgnoreCase("described")) {
                    description = line[5];
                }
                data.add(true, categoryName, x, y, name, description);
            }
            refreshMap();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private VBox radioBox() {
        RadioButton namedRadio = new RadioButton("Named"); // Skapa 2 av den här och lägga dem i vbox
        RadioButton described = new RadioButton("Described"); // Skapa 2 av den här och lägga dem i vbox
        VBox radioBox = new VBox();
        radioBox.getChildren().addAll(namedRadio, described);
        radioBox.setSpacing(6);

        namedRadio.setToggleGroup(toggleGroup);
        described.setToggleGroup(toggleGroup);

        return radioBox;
    }

    private HBox bottomContainer() {
        HBox bottomContainer = new HBox();
        mapPane = new StackPane();
        imageView = new ImageView();
        mapPane.getChildren().addAll(imageView);
        bottomContainer.getChildren().addAll(mapPane, rightPanel());
        return bottomContainer;
    }

    public void refreshMap() {
        mapPane.getChildren().clear();
        mapPane.getChildren().add(imageView);
        for (Map.Entry<Position, NamedPlace> entry : data.getPositionsPlaces().entrySet()) {
            NamedPlace place = entry.getValue();
            Color color = place.getCategory().getColor();
            if (data.isMarked(place)) {
                color = Color.YELLOW;
            }
            place.draw(color);
            place.setOnMouseClicked(new PlaceClickListener(data, place));
            mapPane.getChildren().add(place);
        }
    }

    private VBox rightPanel() {
        VBox rightPanel = new VBox();
        Label categoriesLabel = new Label("Categories");

        List<String> categories = new ArrayList<>();
        categories.add("Train");
        categories.add("Bus");
        categories.add("Underground");
        categoriesListView.setItems(FXCollections.observableList(categories));

        Button hideCatButton = new Button("Hide Categories");
        rightPanel.setSpacing(12);
        rightPanel.setPadding(new Insets(14, 16, 14, 16));

        categoriesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            Set<NamedPlace> places = data.showCategory(obs.getValue());
            if (places != null) {
                if (!mapPane.getChildren().containsAll(places))
                    mapPane.getChildren().addAll(places);
            }
        });

        hideCatButton.setOnAction(e -> {
            Set<NamedPlace> places = data.hideCategory(categoriesListView.getSelectionModel().getSelectedItem());
            if (places != null) {
                mapPane.getChildren().removeAll(places);
            }
        });
        rightPanel.getChildren().addAll(categoriesLabel, categoriesListView, hideCatButton);

        return rightPanel;
    }
}
