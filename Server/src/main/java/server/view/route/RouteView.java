package server.view.route;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RouteView {

    public static void show(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                RouteView.class.getResource("RouteView.fxml")
        );

        Parent root = loader.load();
        Scene scene = new Scene(root, 1100, 700);

        java.net.URL cssUrl = RouteView.class.getResource("AdminView.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle("trenes — Gestión de Rutas");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }
}
