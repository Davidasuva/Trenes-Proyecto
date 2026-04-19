package client.factory;

import client.controller.auth.ClientAuthController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto único de creación de la UI del cliente.
 * Carga el FXML de login, obtiene su controlador y lo devuelve listo.
 * Para abrir el dashboard el propio ClientAuthController llama a
 * ClientFactory.showDashboard(model, stage).
 */
public class ClientFactory {

    private ClientFactory() {}

    /**
     * Muestra la pantalla de login en el Stage recibido.
     * El controlador (ClientAuthController) se autoinyecta via FXML
     * y maneja internamente el cambio hacia el Dashboard.
     */
    public static void showLogin(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ClientFactory.class.getResource(
                            "/client/view/login/ClientLoginView.fxml")
            );

            Parent root  = loader.load();
            Scene  scene = new Scene(root, 720, 480);

            java.net.URL css = ClientFactory.class.getResource(
                    "/client/view/login/ClientLoginView.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            // El controlador ya fue creado por FXMLLoader; lo recuperamos
            // solo para confirmar el tipo (no es necesario inyectarle nada:
            // ClientAuthController se conecta solo en su initialize()).
            ClientAuthController controller = loader.getController();

            stage.setTitle("trenes — Acceso");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creando LoginView del cliente", e);
        }
    }
}
