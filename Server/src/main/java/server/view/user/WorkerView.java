package server.view.user;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

import java.io.IOException;

public class WorkerView {

    public static void show(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                WorkerView.class.getResource("WorkerView.fxml")
        );

        Parent root = loader.load();
        Scene scene = new Scene(root, 1100, 700);

        java.net.URL cssUrl = WorkerView.class.getResource("AdminView.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle("trenes — Trabajadores");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }
}
