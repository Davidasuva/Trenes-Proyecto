package server.view.train;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

import java.io.IOException;

public class TrainView {

    public static void show(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                TrainView.class.getResource("TrainView.fxml")
        );

        Parent root = loader.load();
        Scene scene = new Scene(root, 1100, 700);

        java.net.URL cssUrl = TrainView.class.getResource("AdminView.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle("trenes — Gestión de Trenes");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }
}
