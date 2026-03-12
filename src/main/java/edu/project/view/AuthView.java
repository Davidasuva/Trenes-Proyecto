package edu.project.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthView {

    public static void show(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                AuthView.class.getResource("auth-view.fxml")
        );
        Scene scene = new Scene(loader.load());
        stage.setTitle("Iniciar Sesión - Sistema de Trenes");
        stage.setScene(scene);
        stage.show();
    }
}