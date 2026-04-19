package server.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.controller.auth.AuthController;
import server.model.ServerModel;
import environment.Environment;

/**
 * Único punto de creación de la UI del servidor.
 *
 * <pre>
 *  ServerFactory.showLogin(stage)
 *       └─► carga Login.fxml
 *       └─► inyecta ServerModel al AuthController
 *       └─► AuthController.handleLogin() llama a showServerView()
 *             └─► cambia la escena al panel del servidor
 * </pre>
 */
public class ServerFactory {

    private ServerFactory() {}

    /**
     * Muestra la pantalla de login en el Stage recibido.
     * El ServerModel se crea aquí y se pasa al AuthController,
     * que lo usará al abrir la vista del servidor tras el login.
     */
    public static void showLogin(Stage stage) {
        try {
            Environment env = Environment.getInstance();

            ServerModel model = new ServerModel(
                    env.getIp(),
                    env.getPort(),
                    env.getServiceName()
            );

            FXMLLoader loader = new FXMLLoader(
                    ServerFactory.class.getResource(
                            "/server/view/auth/login/Login.fxml")
            );

            Parent root  = loader.load();
            Scene  scene = new Scene(root, 720, 480);

            java.net.URL css = ServerFactory.class.getResource(
                    "/server/view/auth/login/Login.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            // Inyectar el modelo al AuthController para que lo use al navegar
            AuthController controller = loader.getController();
            controller.setModel(model);

            stage.setTitle("trenes — Iniciar sesión");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creando LoginView del servidor", e);
        }
    }
}
