package contactdatabasegui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/MainStage.fxml"));
            BorderPane root = loader.load();
            MainStageController c = loader.getController();

            Scene scene = new Scene(root, 400, 400);
            scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

            c.setPrimaryStage(primaryStage, scene);
            primaryStage.setTitle("Contact Database - Random Access File Engine");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(700);
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("images/icon/icon2.png"))));
            primaryStage.show();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
