package client.view.purchase;

import client.controller.purchase.PurchaseController;
import client.model.ClientModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import server.model.route.Route;

public class PurchaseView {

    private final Parent root;
    private final PurchaseController controller;

    public PurchaseView(ClientModel model, Route ruta) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("PurchaseView.fxml")
        );
        this.root       = loader.load();
        this.controller = loader.getController();
        controller.init(model, ruta);
    }

    public Parent getView() { return root; }
}
