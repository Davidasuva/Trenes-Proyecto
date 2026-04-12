package server.view.server;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import server.controller.ServerController;
import server.model.ServerModel;

/**
 * Carga Server.fxml e inyecta el modelo al controlador.
 * Es el único punto donde se crea la vista del servidor — no usar ServerFactory.
 */
public class ServerView {

    private final Parent           root;
    private final ServerController controller;

    public ServerView(ServerModel model) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/server/view/server/Server.fxml")
            );

            this.root       = loader.load();
            this.controller = loader.getController();

            controller.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error cargando ServerView");
        }
    }

    public Parent getView() {
        return root;
    }

    public ServerController getController() {
        return controller;
    }
}