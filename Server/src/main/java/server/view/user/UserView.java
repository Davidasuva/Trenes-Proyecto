package server.view.user;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

import java.io.IOException;

public class UserView {

    public static void show(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                UserView.class.getResource("UserView.fxml")
        );

        Parent root = loader.load();
        Scene scene = new Scene(root, 1100, 700);

        java.net.URL cssUrl = UserView.class.getResource("AdminView.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle("trenes — Usuarios");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }
}
