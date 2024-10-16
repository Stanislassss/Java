package com.marketplace;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.marketplace.utils.GestionnaireDonnees;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        GestionnaireDonnees.initializeDatabase();
        Parent root = FXMLLoader.load(getClass().getResource("/com/marketplace/connexion.fxml"));
        primaryStage.setTitle("Marché en ligne");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}