package server.view.employee;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

import java.io.IOException;

public class EmployeeView {

    public static void show(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                EmployeeView.class.getResource("EmployeeView.fxml")
        );

        Parent root = loader.load();
        Scene scene = new Scene(root, 1100, 700);

        java.net.URL cssUrl = EmployeeView.class.getResource("EmployeeView.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle("trenes — Empleados");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }
}
