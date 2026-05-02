package boardingScreen.factory;

import boardingScreen.controller.BoardingOrderController;
import boardingScreen.controller.RouteSelectController;
import boardingScreen.model.BoardingScreenModel;
import environment.Environment;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BoardingFactory {

    private BoardingFactory() {}

    public static void start(Stage stage) {
        try {
            Environment env = Environment.getInstance();
            BoardingScreenModel model = new BoardingScreenModel(
                    env.getIp(), env.getPort(), env.getServiceName());
            showRouteSelect(stage, model);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error iniciando PantallaAbordaje", e);
        }
    }

    public static void showRouteSelect(Stage stage, BoardingScreenModel model) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    BoardingFactory.class.getResource(
                            "/boardingScreen/view/RouteSelectView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 820, 520);

            java.net.URL css = BoardingFactory.class.getResource(
                    "/boardingScreen/view/BoardingStyle.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            RouteSelectController ctrl = loader.getController();
            ctrl.setModel(model);

            stage.setTitle("Abordaje — Seleccionar Ruta");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error mostrando RouteSelectView", e);
        }
    }

    public static void showBoardingOrder(Stage stage, BoardingScreenModel model) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    BoardingFactory.class.getResource(
                            "/boardingScreen/view/BoardingOrderView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 820, 580);

            java.net.URL css = BoardingFactory.class.getResource(
                    "/boardingScreen/view/BoardingStyle.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            BoardingOrderController ctrl = loader.getController();
            ctrl.setModel(model);

            stage.setTitle("Abordaje — Orden de Pasajeros");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error mostrando BoardingOrderView", e);
        }
    }
}
