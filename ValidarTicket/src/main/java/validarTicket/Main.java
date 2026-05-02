package validarTicket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import validarTicket.factory.ValidarFactory;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            ValidarFactory.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error crítico");
            alert.setHeaderText("No se pudo iniciar ValidarTicket");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
