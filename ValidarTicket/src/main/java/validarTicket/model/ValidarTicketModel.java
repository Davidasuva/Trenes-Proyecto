package validarTicket.model;

import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import server.model.route.Route;
import server.model.route.RouteInterface;
import server.model.ticket.Ticket;
import server.model.ticket.TicketInterface;
import server.model.user.AbstractUser;
import server.model.user.Passenger;
import server.model.user.User;

import java.rmi.Naming;

public class ValidarTicketModel {

    private final String ticketUri;
    private final String userUri;
    private final String routeUri;

    private TicketInterface ticketService;
    private User userService;
    private RouteInterface routeService;

    private Route rutaSeleccionada;

    public ValidarTicketModel(String ip, int port, String serviceName) {
        this.ticketUri = "//" + ip + ":" + port + "/" + serviceName;
        this.userUri   = "//" + ip + ":" + port + "/" + serviceName + "-users";
        this.routeUri  = "//" + ip + ":" + port + "/" + serviceName + "-routes";
    }

    public boolean connect() {
        try {
            ticketService = (TicketInterface) Naming.lookup(ticketUri);
            userService   = (User)            Naming.lookup(userUri);
            routeService  = (RouteInterface)  Naming.lookup(routeUri);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public LinkedList<Route> getAvailableRoutes() {
        try {
            return routeService.getAvailableRoutes();
        } catch (Exception e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public enum ResultadoValidacion {
        PASAJERO_NO_ENCONTRADO,
        SIN_TICKETS_EN_RUTA,
        VALIDADO
    }

    public ResultadoValidacion validar(String nombre, String correo, String password) {
        try {
            AbstractUser user = userService.userPerEmailAndPassword(correo, password, null);
            if (user == null || !(user instanceof Passenger)) {
                return ResultadoValidacion.PASAJERO_NO_ENCONTRADO;
            }
            Passenger pasajero = (Passenger) user;

            String nombreCompleto = (pasajero.getName() + " " + pasajero.getLastName()).toLowerCase();
            if (!nombreCompleto.contains(nombre.trim().toLowerCase())) {
                return ResultadoValidacion.PASAJERO_NO_ENCONTRADO;
            }

            LinkedList<Ticket> tickets = ticketService.seeTicketsPerPassenger(pasajero);
            Iterator<Ticket> it = tickets.iterator();
            while (it.hasNext()) {
                Ticket t = it.next();
                if (t.Status()
                        && t.getRoute() != null
                        && rutaSeleccionada != null
                        && t.getRoute().getId() == rutaSeleccionada.getId()) {
                    return ResultadoValidacion.VALIDADO;
                }
            }
            return ResultadoValidacion.SIN_TICKETS_EN_RUTA;

        } catch (Exception e) {
            e.printStackTrace();
            return ResultadoValidacion.PASAJERO_NO_ENCONTRADO;
        }
    }

    public void setRutaSeleccionada(Route ruta) { this.rutaSeleccionada = ruta; }
    public Route getRutaSeleccionada() { return rutaSeleccionada; }
}
