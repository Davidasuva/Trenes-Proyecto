package edu.project.view.auth;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterView {

    public static void show(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                RegisterView.class.getResource("register-view.fxml")
        );

        Parent root = loader.load();
        Scene scene = new Scene(root, 720, 480);

        java.net.URL cssUrl = RegisterView.class.getResource("auth-view.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle("trenes — Registro");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }
}