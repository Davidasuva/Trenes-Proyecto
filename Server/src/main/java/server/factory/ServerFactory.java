package server.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import server.controller.ServerController;
import server.model.ServerModel;
import environment.Environment;

public class ServerFactory {

    private ServerFactory(){}

    public static Parent create() {
        try {
            Environment env = Environment.getInstance();

            ServerModel model = new ServerModel(
                    env.getIp(),
                    env.getPort(),
                    env.getServiceName()
            );

            FXMLLoader loader = new FXMLLoader(
                    ServerFactory.class.getResource("/server/view/server/Server.fxml")
            );

            Parent root = loader.load();

            ServerController controller = loader.getController();
            controller.setModel(model);

            return root;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creando ServerView");
        }
    }
}