import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.*;

public class Main  extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox mainContainer = new VBox();

        HBox topContainer = new HBox();
        topContainer.setMinHeight(200);
        topContainer.setMinWidth(600);
        topContainer.setBackground(new Background(new BackgroundFill(Color.AQUA, null, null)));

        HBox bottomContainer = new HBox();
        bottomContainer.setMinHeight(400);
        bottomContainer.setMinWidth(600);
        bottomContainer.setBackground(new Background(new BackgroundFill(Color.BISQUE, null, null)));

        Button button; // Skapa 5 stycken
        RadioButton radioButton; // Skapa 2 av den här och lägga dem i vbox
        ButtonGroup buttonGroup = new ButtonGroup(); // Lägg radio buttons i den här

        TextField textField;

        /*
        Lägga alla knappar och textfältet till topContainer
         */

        mainContainer.getChildren().addAll(topContainer, bottomContainer);

        Scene scene = new Scene(mainContainer, 600, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
