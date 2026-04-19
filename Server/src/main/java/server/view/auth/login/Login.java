package server.view.auth.login;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
public class Login {

    private Login() {}

    public static void show(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Login.class.getResource("Login.fxml")
        );

        Parent root  = loader.load();
        Scene  scene = new Scene(root, 720, 480);

        java.net.URL cssUrl = Login.class.getResource("Login.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle("trenes — Iniciar sesión");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }
}