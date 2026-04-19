package client.view.dashboard;

import client.controller.dashboard.DashboardController;
import client.model.ClientModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class DashboardView {

    private final Parent              root;
    private final DashboardController controller;

    public DashboardView(ClientModel model) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("DashboardView.fxml")
        );
        this.root       = loader.load();
        this.controller = loader.getController();
        controller.setModel(model);
    }

    public Parent getView() { return root; }
}