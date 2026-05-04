package client.controller.dashboard;

import client.controller.purchase.PurchaseController;
import client.factory.ClientFactory;
import client.model.ClientModel;
import client.view.purchase.PurchaseView;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import server.model.observer.Observer;
import server.model.route.Route;
import server.model.route.RouteGraph;
import server.model.route.Station;

import java.util.ArrayList;
import java.util.List;

public class DashboardController {

    @FXML private Label lblBienvenido;
    @FXML private VBox  vboxRutas;
    @FXML private ComboBox<String> cmbFiltroOrigen;
    @FXML private ComboBox<String> cmbFiltroDestino;
    @FXML private Label lblLog;

    private ClientModel model;
    private List<Route> todasLasRutas = new ArrayList<>();
    private RouteGraph routeGraph;

    public void setModel(ClientModel model) {
        this.model = model;
        try { routeGraph = new RouteGraph(); } catch (Exception e) { routeGraph = null; }
        initUI();
        subscribeToModel();
        refreshRoutes();
    }

    private void initUI() {
        lblBienvenido.setText("Hola, " + model.getCurrentPassenger().getName()
                + "  •  " + model.getCurrentPassenger().getMail());
    }

    private void subscribeToModel() {
        new Observer(model) {
            @Override
            public void update() {
                Platform.runLater(() -> lblLog.setText(model.getLogger()));
            }
        };
    }

    @FXML
    public void refreshRoutes() {
        lblLog.setText("Cargando rutas...");
        new Thread(() -> {
            LinkedList<Route> routes = model.getAvailableRoutes();
            Platform.runLater(() -> {
                todasLasRutas.clear();
                if (routes != null) {
                    Iterator<Route> it = routes.iterator();
                    while (it.hasNext()) todasLasRutas.add(it.next());
                }
                poblarFiltros();
                renderRutas(todasLasRutas);
                lblLog.setText(todasLasRutas.isEmpty()
                        ? "No hay rutas publicadas disponibles."
                        : todasLasRutas.size() + " ruta(s) disponibles.");
            });
        }).start();
    }

    private void poblarFiltros() {
        ObservableList<String> stations = FXCollections.observableArrayList();
        stations.add("Todas las estaciones");
        if (routeGraph != null) {
            LinkedList<Station> sl = routeGraph.getStations();
            Iterator<Station> it = sl.iterator();
            while (it.hasNext()) stations.add(it.next().getName());
        }
        cmbFiltroOrigen.setItems(stations);
        cmbFiltroDestino.setItems(FXCollections.observableArrayList(stations));
        cmbFiltroOrigen.setValue("Todas las estaciones");
        cmbFiltroDestino.setValue("Todas las estaciones");
    }

    @FXML
    public void aplicarFiltros() {
        String origen  = cmbFiltroOrigen.getValue();
        String destino = cmbFiltroDestino.getValue();

        List<Route> filtradas = new ArrayList<>();
        for (Route r : todasLasRutas) {
            boolean matchOrigen  = origen  == null || origen.equals("Todas las estaciones")
                    || r.getOrigin().getName().equals(origen);
            boolean matchDestino = destino == null || destino.equals("Todas las estaciones")
                    || r.getDestiny().getName().equals(destino);
            if (matchOrigen && matchDestino) filtradas.add(r);
        }
        renderRutas(filtradas);
        lblLog.setText(filtradas.size() + " ruta(s) encontrada(s).");
    }

    @FXML
    public void limpiarFiltros() {
        cmbFiltroOrigen.setValue("Todas las estaciones");
        cmbFiltroDestino.setValue("Todas las estaciones");
        renderRutas(todasLasRutas);
        lblLog.setText(todasLasRutas.size() + " ruta(s) disponibles.");
    }

    private void renderRutas(List<Route> rutas) {
        vboxRutas.getChildren().clear();
        if (rutas.isEmpty()) {
            Label empty = new Label("No se encontraron rutas con los filtros seleccionados.");
            empty.setStyle("-fx-font-family:'Georgia'; -fx-font-size:13px; -fx-text-fill:#9AA3AE;");
            vboxRutas.getChildren().add(empty);
            return;
        }
        for (Route r : rutas) {
            vboxRutas.getChildren().add(crearCardRuta(r));
        }
    }

    private VBox crearCardRuta(Route ruta) {
        VBox card = new VBox(8);
        card.getStyleClass().add("ruta-card");

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label nombre = new Label(ruta.getName());
        nombre.getStyleClass().add("ruta-nombre");


        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        boolean enCurso = ruta.getDateTravel() != null && ahora.isAfter(ruta.getDateTravel());
        Label chip = enCurso ? new Label("En curso") : new Label("Publicada");
        chip.getStyleClass().add(enCurso ? "chip-en-curso" : "chip-activo");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topRow.getChildren().addAll(nombre, spacer, chip);


        Label trayecto = new Label(ruta.getOrigin().getName() + "  →  " + ruta.getDestiny().getName());
        trayecto.getStyleClass().add("ruta-trayecto");

        HBox infoRow = new HBox(24);
        infoRow.setAlignment(Pos.CENTER_LEFT);
        Label dist = new Label("📏  " + String.format("%.0f km", ruta.getTotalDistance()));
        dist.getStyleClass().add("ruta-distancia");
        Label fechas = new Label("🕐  " + ruta.getDateTravelStr()
                + "   →   " + ruta.getDateArrivalStr());
        fechas.getStyleClass().add("ruta-fechas");
        infoRow.getChildren().addAll(dist, fechas);

        String estacionesStr = obtenerEstaciones(ruta);
        Label estLabel = new Label("ESTACIONES INTERMEDIAS");
        estLabel.getStyleClass().add("ruta-estaciones-label");
        Label estaciones = new Label(estacionesStr);
        estaciones.getStyleClass().add("ruta-estaciones");
        estaciones.setWrapText(true);

        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.CENTER_RIGHT);
        Button btnComprar = new Button("Comprar tiquete →");
        btnComprar.getStyleClass().add("btn-comprar-card");
        btnComprar.setOnAction(e -> abrirCompra(ruta));
        bottomRow.getChildren().add(btnComprar);

        card.getChildren().addAll(topRow, trayecto, infoRow, estLabel, estaciones, bottomRow);

        card.setOnMouseClicked(e -> {
            if (e.getTarget() != btnComprar) abrirCompra(ruta);
        });

        return card;
    }

    private String obtenerEstaciones(Route ruta) {
        if (routeGraph == null) {
            return ruta.getOrigin().getName() + "  →  " + ruta.getDestiny().getName();
        }
        try {
            LinkedList<Station> path = routeGraph.getShortestPath(ruta.getOrigin(), ruta.getDestiny());
            StringBuilder sb = new StringBuilder();
            Iterator<Station> it = path.iterator();
            boolean first = true;
            while (it.hasNext()) {
                if (!first) sb.append("  →  ");
                sb.append(it.next().getName());
                first = false;
            }
            return sb.toString();
        } catch (Exception e) {
            return ruta.getOrigin().getName() + "  →  " + ruta.getDestiny().getName();
        }
    }

    private void abrirCompra(Route ruta) {
        try {
            Stage stage = (Stage) vboxRutas.getScene().getWindow();
            PurchaseView pv = new PurchaseView(model, ruta);
            stage.setScene(new Scene(pv.getView(), 960, 640));
            stage.setTitle("trenes — Comprar tiquete");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            lblLog.setText("Error al abrir compra: " + e.getMessage());
        }
    }

    @FXML
    public void handleRecomendarRuta() {
        String origen  = cmbFiltroOrigen.getValue();
        String destino = cmbFiltroDestino.getValue();

        if (origen == null || origen.isEmpty() || destino == null || destino.isEmpty()
                || "Todas".equals(origen) || "Todas".equals(destino)) {
            lblLog.setText("Selecciona un origen y un destino para recibir una recomendación.");
            return;
        }
        if (origen.equals(destino)) {
            lblLog.setText("El origen y el destino no pueden ser iguales.");
            return;
        }

        Route mejorRuta = null;
        double menorDistancia = Double.MAX_VALUE;
        for (Route r : todasLasRutas) {
            boolean origenOk  = r.getOrigin().getName().equalsIgnoreCase(origen);
            boolean destinoOk = r.getDestiny().getName().equalsIgnoreCase(destino);
            if (origenOk && destinoOk && r.getTotalDistance() < menorDistancia) {
                mejorRuta = r;
                menorDistancia = r.getTotalDistance();
            }
        }

        if (mejorRuta == null) {
            lblLog.setText("No se encontró ninguna ruta de " + origen + " → " + destino + ".");
            return;
        }

        final Route rutaRecomendada = mejorRuta;
        lblLog.setText("✔  Ruta recomendada: " + rutaRecomendada.getName() +  " (" + String.format("%.0f", rutaRecomendada.getTotalDistance()) + " km");
        abrirCompra(rutaRecomendada);
    }

    @FXML
    public void handleHistorial() {
        try {
            Stage stage = (Stage) vboxRutas.getScene().getWindow();
            client.view.history.TicketHistoryView view = new client.view.history.TicketHistoryView(model);
            stage.setScene(new Scene(view.getView(), 1100, 680));
            stage.setTitle("trenes — Mis tiquetes");
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            lblLog.setText("Error al abrir historial: " + e.getMessage());
        }
    }

    @FXML
    public void handleLogout() {
        model.logout();
        try {
            Stage stage = (Stage) vboxRutas.getScene().getWindow();
            stage.setResizable(false);
            ClientFactory.showLogin(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
