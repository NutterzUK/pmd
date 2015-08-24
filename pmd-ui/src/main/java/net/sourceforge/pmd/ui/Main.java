package net.sourceforge.pmd.ui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/layout.fxml"));
            Parent root = null;
            root = (Parent)loader.load();
            Controller controller = (Controller)loader.getController();
            controller.setStageAndSetupListeners(primaryStage);
            primaryStage.setTitle("PMD Ruleset Editor");
            primaryStage.setScene(new Scene(root, 1024, 768));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
