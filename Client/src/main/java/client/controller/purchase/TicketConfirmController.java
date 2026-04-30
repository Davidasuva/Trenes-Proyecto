package client.controller.purchase;

import client.model.ClientModel;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.model.carriage.AbstractCarriage;
import server.model.carriage.CarriageLoad;
import server.model.carriage.CarriagePassenger;
import server.model.luggage.Luggage;
import server.model.route.Route;
import server.model.route.RouteGraph;
import server.model.route.Station;
import server.model.ticket.Ticket;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import edu.uva.app.array.Array;

public class TicketConfirmController {

    @FXML private Label lblTicketId;
    @FXML private Label lblId;
    @FXML private Label lblUserId;
    @FXML private Label lblPasajero;
    @FXML private Label lblFechaCompra;
    @FXML private Label lblCategoria;
    @FXML private Label lblPrecio;
    @FXML private Label lblRuta;
    @FXML private Label lblOrigen;
    @FXML private Label lblDestino;
    @FXML private Label lblDistancia;
    @FXML private Label lblSalida;
    @FXML private Label lblLlegada;
    @FXML private Label lblEstaciones;
    @FXML private Label lblTren;
    @FXML private Label lblVagonPasajero;
    @FXML private Label lblVagonMaleta;
    @FXML private VBox  vboxMaletas;
    @FXML private Label lblContactoNombre;
    @FXML private Label lblContactoApellido;
    @FXML private Label lblContactoTelefono;

    private final ClientModel model;
    private Ticket ticket;

    public TicketConfirmController(ClientModel model) {
        this.model = model;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        poblarUI();
    }

    private void poblarUI() {
        Route ruta = ticket.getRoute();
        // Corregido: 0=Premium, 1=Ejecutivo, 2=Estándar
        String[] categorias = {"Premium", "Ejecutivo", "Estándar"};
        int cat = ticket.getCategory();

        lblTicketId.setText("Tiquete # " + ticket.getId());
        lblId.setText(ticket.getId());
        lblUserId.setText(ticket.getPassenger().getId());
        lblPasajero.setText(ticket.getPassenger().getName()
                + " " + ticket.getPassenger().getLastName());
        lblFechaCompra.setText(ticket.getDateBuy());
        lblCategoria.setText(cat >= 0 && cat <= 2 ? categorias[cat] : String.valueOf(cat));
        lblPrecio.setText(String.format("$ %,d COP", ticket.getPrice()));

        lblRuta.setText(ruta.getName() + "  (ID: " + ruta.getId() + ")");
        lblOrigen.setText(ruta.getOrigin().getName());
        lblDestino.setText(ruta.getDestiny().getName());
        lblDistancia.setText(String.format("%.0f km", ruta.getTotalDistance()));
        lblSalida.setText(ruta.getDateTravelStr());
        lblLlegada.setText(ruta.getDateArrivalStr());

        // Estaciones intermedias
        try {
            RouteGraph graph = new RouteGraph();
            LinkedList<Station> path = graph.getShortestPath(ruta.getOrigin(), ruta.getDestiny());
            StringBuilder sb = new StringBuilder();
            Iterator<Station> it = path.iterator();
            boolean first = true;
            while (it.hasNext()) {
                Station s = it.next();
                if (!first) sb.append("  →  ");
                sb.append(s.getName());
                first = false;
            }
            lblEstaciones.setText(sb.toString());
        } catch (Exception e) {
            lblEstaciones.setText(ruta.getOrigin().getName() + "  →  " + ruta.getDestiny().getName());
        }

        // Tren y vagones
        if (ticket.getTrain() != null) {
            lblTren.setText(ticket.getTrain().getName()
                    + "  (ID: " + ticket.getTrain().getId() + ")");
        } else {
            lblTren.setText("—");
        }

        if (ticket.getCarriagePassenger() != null) {
            CarriagePassenger cp = ticket.getCarriagePassenger();
            lblVagonPasajero.setText("Vagón #" + cp.getId()
                    + "  (" + cp.getActualCapacity() + "/" + cp.getMaxCapacity() + " pasajeros)");
        } else {
            lblVagonPasajero.setText("—");
        }

        if (ticket.getCarriageLoad() != null) {
            CarriageLoad cl = ticket.getCarriageLoad();
            lblVagonMaleta.setText("Vagón #" + cl.getId()
                    + "  (carga: " + cl.getActualWeight() + "/" + cl.getMaxCapacity() + " kg)");
        } else {
            lblVagonMaleta.setText("—");
        }

        // Persona de contacto
        if (lblContactoNombre != null)   lblContactoNombre.setText(ticket.getContactName() != null   ? ticket.getContactName()   : "—");
        if (lblContactoApellido != null) lblContactoApellido.setText(ticket.getContactLastName() != null ? ticket.getContactLastName() : "—");
        if (lblContactoTelefono != null) lblContactoTelefono.setText(ticket.getContactPhone() != null  ? ticket.getContactPhone()  : "—");

        // Maletas
        vboxMaletas.getChildren().clear();
        Array<Luggage> luggages = ticket.getLuggage();
        boolean tieneMaletas = false;
        for (int i = 0; i < luggages.size(); i++) {
            Luggage l = luggages.get(i);
            if (l != null) {
                tieneMaletas = true;
                HBox row = new HBox(10);
                row.getStyleClass().add("maleta-info-row");
                Label lbl = new Label("🧳  Maleta " + (i + 1) + ":  " + l.getWeight() + " kg  —  Vagón #"
                        + (l.getCarriage() != null ? l.getCarriage().getId() : "—"));
                lbl.getStyleClass().add("maleta-info-text");
                row.getChildren().add(lbl);
                vboxMaletas.getChildren().add(row);
            }
        }
        if (!tieneMaletas) {
            Label noMal = new Label("Sin maletas registradas.");
            noMal.getStyleClass().add("no-maletas");
            vboxMaletas.getChildren().add(noMal);
        }
    }

    @FXML public void handleVolver() {
        try {
            Stage stage = (Stage) vboxMaletas.getScene().getWindow();
            client.view.dashboard.DashboardView dash = new client.view.dashboard.DashboardView(model);
            stage.setScene(new Scene(dash.getView(), 960, 640));
            stage.setTitle("trenes — " + model.getCurrentPassenger().getName());
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
