package validarTicket.factory;

import environment.Environment;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import validarTicket.controller.RouteSelectController;
import validarTicket.controller.ValidarPasajeroController;
import validarTicket.model.ValidarTicketModel;

public class ValidarFactory {

    private ValidarFactory() {}

    public static void start(Stage stage) {
        Environment env = Environment.getInstance();
        ValidarTicketModel model = new ValidarTicketModel(
                env.getIp(), env.getPort(), env.getServiceName());
        showRouteSelect(stage, model);
    }

    public static void showRouteSelect(Stage stage, ValidarTicketModel model) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ValidarFactory.class.getResource("/validarTicket/view/RouteSelectView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 820, 520);
            applyStyle(scene);

            RouteSelectController ctrl = loader.getController();
            ctrl.setModel(model);

            stage.setTitle("Validar Ticket — Seleccionar Ruta");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error mostrando RouteSelectView", e);
        }
    }

    public static void showValidarPasajero(Stage stage, ValidarTicketModel model) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ValidarFactory.class.getResource("/validarTicket/view/ValidarPasajeroView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 820, 580);
            applyStyle(scene);

            ValidarPasajeroController ctrl = loader.getController();
            ctrl.setModel(model);

            stage.setTitle("Validar Ticket — " + model.getRutaSeleccionada().getName());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error mostrando ValidarPasajeroView", e);
        }
    }

    private static void applyStyle(Scene scene) {
        java.net.URL css = ValidarFactory.class.getResource("/validarTicket/view/ValidarStyle.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
    }
}
