import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main  extends Application {

    private Stage primaryStage;
    private Data data;
    private ImageView imageView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.data = new Data();

        VBox mainContainer = new VBox();

        mainContainer.getChildren().addAll(menuBar(), topContainer(), bottomContainer());

        Scene scene = new Scene(mainContainer, 1100, 800);
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
        });

        hideButton.setOnAction(e -> {
            data.hide();
            System.out.println("-------------Hidden--------------");
            data.printHidden();
            System.out.println("-------------Marked---------------");
            data.printMarked();
        });

        removeButton.setOnAction(e -> {
            data.remove();
            System.out.println("-------------All Places--------------");
            data.printPlaces();
            System.out.println("-------------Hidden--------------");
            data.printHidden();
            System.out.println("-------------Marked---------------");
            data.printMarked();

        });

        topContainer.getChildren().addAll(newButton, radioBox(), textField,
                searchButton, removeButton, hideButton, coordinatesButton);

        return topContainer;
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
        });

        fileMenu.getItems().addAll(loadMap, loadPlaces, save, exit);
        menuBar.getMenus().add(fileMenu);
        return menuBar;
    }

    private void loadPlaces(File file) {
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
        ToggleGroup toggleGroup = new ToggleGroup(); // Lägg radio buttons i den här
        namedRadio.setToggleGroup(toggleGroup);
        described.setToggleGroup(toggleGroup);

        return radioBox;
    }

    private HBox bottomContainer() {
        HBox bottomContainer = new HBox();
        StackPane mapStack = new StackPane();
        imageView = new ImageView();

        try {
            InputStream inputStream = new FileInputStream("C:/AwesomeMaps/exempelkarta.png");
            imageView.setImage(new Image(inputStream));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        mapStack.getChildren().addAll(imageView);
        bottomContainer.getChildren().addAll(mapStack, rightPanel());
        return bottomContainer;
    }

    private VBox rightPanel() {
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
