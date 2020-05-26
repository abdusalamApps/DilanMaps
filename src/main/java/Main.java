import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Data;
import model.Position;
import model.categories.Category;
import model.places.DescribedPlace;
import model.places.Place;

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
    private DefaultMapListener defaultMapListener;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.data = new Data();
        toggleGroup = new ToggleGroup();
        categoriesListView = new ListView<>();
        VBox mainContainer = new VBox();
        mapPane = new StackPane();
        newPlaceCategory = "";
        defaultMapListener = new DefaultMapListener(data, this);

        categoriesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            newPlaceCategory = newV;
        });

        mainContainer.getChildren().addAll(menuBar(), topContainer(), bottomContainer());
        scene = new Scene(mainContainer, 1100, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
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
            data.search(textField.getText().trim());
            System.out.println("-------------Marked---------------");
            data.printMarked();
            refreshMap(data.getPlaces());
        });

        hideButton.setOnAction(e -> {
            data.hide();
            System.out.println("-------------Hidden--------------");
            data.printHidden();
            System.out.println("-------------Marked---------------");
            data.printMarked();
            refreshMap(data.getPlaces());
        });

        removeButton.setOnAction(e -> {
            data.remove();
            System.out.println("-------------All Places--------------");
            data.printPlaces();
            System.out.println("-------------Hidden--------------");
            data.printHidden();
            System.out.println("-------------Marked---------------");
            data.printMarked();
            refreshMap(data.getPlaces());

        });

        coordinatesButton.setOnAction(e -> {
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
                    String place = data.placeByCoordinates(x, y);
                    if (place == null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("Place Not Found!");
                        alert.show();
                    } else {
                        // Mark the place
                        data.mark(x, y);
                        System.out.println("-------------Marked---------------");
                        data.printMarked();
                        refreshMap(data.getPlaces());
                    }
                }
                return null;
            });

            mainBox.getChildren().addAll(xBox, yBox);
            dialog.getDialogPane().getButtonTypes().addAll(okType, cancelType);
            dialog.getDialogPane().setContent(mainBox);
            dialog.show();
        });

        newButton.setOnAction(e -> {
            saveNewPlace();
        });

        topContainer.getChildren().addAll(newButton, radioBox(), textField,
                searchButton, removeButton, hideButton, coordinatesButton);

        return topContainer;
    }

    private void saveNewPlace() {
        scene.setCursor(Cursor.CROSSHAIR);
        mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                double clickedX = e.getX();
                double clickedY = e.getY();
                boolean placeExist = false;
                for (Map.Entry<Position, Place> entry : data.getPlaces().entrySet()) {
                    int x = entry.getKey().x;
                    int y = entry.getKey().y;
                    if (Math.abs(clickedX - x) < 7 && Math.abs(e.getY() - y) < 7) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("det är endast tillåtet med en plats per position");
                        alert.show();
                        placeExist = true;
                        break;
                    }
                }
                if (!placeExist) {
                    RadioButton radioButton = (RadioButton) toggleGroup.getSelectedToggle();
                    if (radioButton.getText().equalsIgnoreCase("named")) {
                        // Save a new Named Place
                        saveNamedPlace(clickedX, clickedY);
                        scene.setCursor(Cursor.DEFAULT);
                        mapPane.setOnMouseClicked(defaultMapListener);
                    } else if (radioButton.getText().equalsIgnoreCase("described")) {
                        // Save a new DescribedPlace
                        saveDescribedPlace(clickedX, clickedY);
                        scene.setCursor(Cursor.DEFAULT);
                        mapPane.setOnMouseClicked(defaultMapListener);
                    }
                }
            }
        });
    }

    private void saveNamedPlace(double x, double y) {
        /*
        1. Show a dialog
        2. Save place when clicking save
         */
        Dialog<String> dialog = new Dialog<>();
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        Label label = new Label("Name");
        TextField field = new TextField();
        vBox.getChildren().addAll(label, field);

        ButtonType okType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.setResultConverter(buttonType -> {
            if (buttonType.equals(okType)) {
                data.add((int) x, (int) y, field.getText().trim(), newPlaceCategory);
                refreshMap(data.getPlaces());
            }
            return null;
        });


        dialog.getDialogPane().getButtonTypes().addAll(okType, cancelType);
        dialog.getDialogPane().setContent(vBox);
        dialog.show();
    }

    private void saveDescribedPlace(double x, double y) {
        Dialog<String> dialog = new Dialog<>();
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        Label label = new Label("Name ");
        TextField field = new TextField();
        Label dLabel = new Label("Description");
        TextField dField = new TextField();

        vBox.getChildren().addAll(label, field, dLabel, dField);

        ButtonType okType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.setResultConverter(buttonType -> {
            if (buttonType.equals(okType)) {
                data.add((int) x, (int) y, field.getText().trim(), newPlaceCategory, dField.getText().trim());
                refreshMap(data.getPlaces());
            }
            return null;
        });


        dialog.getDialogPane().getButtonTypes().addAll(okType, cancelType);
        dialog.getDialogPane().setContent(vBox);
        dialog.show();
    }

    private MenuBar menuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem loadMap = new MenuItem("Load Map");
        MenuItem loadPlaces = new MenuItem("Load Places");
        MenuItem save = new MenuItem("Save");
        MenuItem exit = new MenuItem("Exit");

        loadMap.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose a Map");
            File file = chooser.showOpenDialog(primaryStage);
            try {
                FileInputStream inputStream = new FileInputStream(file);
                imageView.setImage(new Image(inputStream));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        loadPlaces.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose Places File");
            File file = chooser.showOpenDialog(primaryStage);
            loadPlaces(file);
            System.out.println("-------------All Places--------------");
            data.printPlaces();
            System.out.println("-------------Marked---------------");
            data.printMarked();

        });

        fileMenu.getItems().addAll(loadMap, loadPlaces, save, exit);
        menuBar.getMenus().add(fileMenu);
        return menuBar;
    }

    private void loadPlaces(File file) {
        data.clear();

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] array = line.split(",");
                String type = array[0];
                String categoryName = array[1];
                int x = Integer.parseInt(array[2]);
                int y = Integer.parseInt(array[3]);
                String name = array[4];
                if (type.equalsIgnoreCase("described")) {
                    String description = array[5];
                    // Add Described place
                    data.add(x, y, name, description, categoryName);
                } else {
                    // Add a named place
                    data.add(x, y, name, categoryName);
                }
            }
            refreshMap(data.getPlaces());
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

        try {
            InputStream inputStream = new FileInputStream("C:/AwesomeMaps/exempelkarta.png");
            imageView.setImage(new Image(inputStream));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        mapPane.setOnMouseClicked(defaultMapListener);

        mapPane.getChildren().addAll(imageView);
        refreshMap(data.getPlaces());
        bottomContainer.getChildren().addAll(mapPane, rightPanel());
        return bottomContainer;
    }

    public void refreshMap(Map<Position, Place> placeMap) {
        mapPane.getChildren().clear();
        mapPane.getChildren().add(imageView);

        for (Map.Entry<Position, Place> entry : placeMap.entrySet()) {
            double x = entry.getKey().x;
            double y = entry.getKey().y;

            Polyline polyline = new Polyline();
            polyline.setManaged(false);

            polyline.getPoints().addAll(
                    x - 10, y - 10,
                    x + 10, y - 10,
                    x, y,
                    x - 10, y - 10
            );

            Category category = entry.getValue().getCategory();

            if (data.isMarked(x, y)) {
                polyline.setFill(Color.YELLOW);
            } else {
                polyline.setFill(category.getColor());
            }

            if (!data.isHidden(x, y)) {
                mapPane.getChildren().add(polyline);
            }

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
            data.showCategory(obs.getValue());
            refreshMap(data.getPlaces());
        });

        hideCatButton.setOnAction(e -> {
            data.hideCategory(categoriesListView.getSelectionModel().getSelectedItem());
            refreshMap(data.getPlaces());
        });
        rightPanel.getChildren().addAll(categoriesLabel, categoriesListView, hideCatButton);

        return rightPanel;
    }
}
