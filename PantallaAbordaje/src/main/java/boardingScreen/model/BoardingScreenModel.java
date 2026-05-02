package boardingScreen.model;

import edu.uva.app.linkedlist.singly.singly.LinkedList;
import server.model.observer.Subject;
import server.model.route.Route;
import server.model.route.RouteInterface;
import server.model.ticket.TicketInterface;
import server.model.user.User;

import java.rmi.Naming;

public class BoardingScreenModel extends Subject {
    private final String uri;
    private final String userUri;
    private final String routeUri;

    private TicketInterface ticketService;
    private User userService;
    private RouteInterface routeService;

    private String logger;
    private Route currentRoute;

    public BoardingScreenModel(String ip, int port, String serviceName) {
        this.uri      = "//" + ip + ":" + port + "/" + serviceName;
        this.userUri  = "//" + ip + ":" + port + "/" + serviceName + "-users";
        this.routeUri = "//" + ip + ":" + port + "/" + serviceName + "-routes";
    }

    public boolean connect() {
        try {
            ticketService = (TicketInterface) Naming.lookup(uri);
            userService   = (User)            Naming.lookup(userUri);
            routeService  = (RouteInterface)  Naming.lookup(routeUri);
            log("Conectado al servidor: " + uri);
            return true;
        } catch (Exception e) {
            log("No se pudo conectar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public LinkedList<Route> getAvailableRoutes() {
        try {
            return routeService.getAvailableRoutes();
        } catch (Exception e) {
            log("Error obteniendo rutas: " + e.getMessage());
            return new LinkedList<>();
        }
    }

    public LinkedList<String> getBoardingOrder(int routeId) {
        try {
            return routeService.getBoardingOrder(routeId);
        } catch (Exception e) {
            log("Error obteniendo orden de abordaje: " + e.getMessage());
            LinkedList<String> err = new LinkedList<>();
            err.add("Error: " + e.getMessage());
            return err;
        }
    }

    public void setCurrentRoute(Route route) { this.currentRoute = route; }
    public Route getCurrentRoute() { return currentRoute; }
    public String getLogger() { return logger; }

    private void log(String msg) {
        this.logger = msg;
        this.notifyObservers();
    }
}
