package client.model;

import java.rmi.Naming;
import java.util.List;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import server.model.carriage.AbstractCarriage;
import server.model.carriage.CarriageLoad;
import server.model.carriage.CarriagePassenger;
import server.model.observer.Subject;
import server.model.route.Route;
import server.model.route.RouteInterface;
import server.model.ticket.Ticket;
import server.model.ticket.TicketInterface;
import server.model.user.AbstractUser;
import server.model.user.Passenger;
import server.model.user.User;
import server.model.luggage.Luggage;

public class ClientModel extends Subject {
    private final String uri;
    private final String userUri;
    private final String routeUri;

    private TicketInterface ticketService;
    private User userService;
    private RouteInterface routeService;

    private String logger;
    private Passenger currentPassenger;

    public ClientModel(String ip, int port, String serviceName) {
        this.uri      = "//" + ip + ":" + port + "/" + serviceName;
        this.userUri  = "//" + ip + ":" + port + "/" + serviceName + "-users";
        this.routeUri = "//" + ip + ":" + port + "/" + serviceName + "-routes";
    }

    public boolean connect() {
        try {
            ticketService = (TicketInterface) Naming.lookup(uri);
            userService   = (User)            Naming.lookup(userUri);
            routeService  = (RouteInterface)  Naming.lookup(routeUri);
            log("Conectando al servidor: " + uri);
            return true;
        } catch (Exception e) {
            log("No se pudo conectar a: " + uri + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean connect(String email, String password) {
        try {
            AbstractUser user = userService.userPerEmailAndPassword(email, password, null);
            if (user == null) { log("Credenciales incorrectas."); return false; }
            if (!(user instanceof Passenger)) {
                log("Esta aplicación es solo para pasajeros.");
                return false;
            }
            currentPassenger = (Passenger) user;
            log("Bienvenido " + currentPassenger.getName() + "!");
            return true;
        } catch (Exception e) {
            log("Error en login: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerPassenger(String id, String mail, String name, String lastName,
                                     String password, String typeIdentification, String address) {
        try {
            Passenger p = new Passenger(id, mail, name, lastName, password, typeIdentification, address);
            AbstractUser registered = userService.register(p);
            currentPassenger = (Passenger) registered;
            log("Cuenta registrada con éxito. Bienvenido: " + currentPassenger.getName());
            return true;
        } catch (Exception e) {
            log("Error al registrar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public LinkedList<Route> getAvailableRoutes() {
        try {
            LinkedList<Route> routes = routeService.getAvailableRoutes();
            log("Se encontraron: " + routes.size() + " rutas disponibles");
            return routes;
        } catch (Exception e) {
            log("Error al obtener rutas: " + e.getMessage());
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    /** Compra sin maletas (compatibilidad). */
    public Ticket buyTicket(Route route, int category) {
        return buyTicket(route, category, null);
    }

    /** Compra con lista opcional de maletas. */
    public Ticket buyTicket(Route route, int category, List<Luggage> maletas) {
        if (currentPassenger == null) {
            log("Debe iniciar sesión para poder comprar un ticket.");
            return null;
        }
        try {
            // Obtener el tren de la ruta
            server.model.train.Train train = route.getTrains().peek();
            if (train == null) {
                log("La ruta no tiene un tren asignado.");
                return null;
            }

            // Buscar vagón de pasajeros con capacidad disponible
            CarriagePassenger carriagePassenger = null;
            CarriageLoad carriageLoad = null;

            Iterator<AbstractCarriage> itC = train.getCarriages().iterator();
            while (itC.hasNext()) {
                AbstractCarriage c = itC.next();
                if (c instanceof CarriagePassenger && carriagePassenger == null) {
                    CarriagePassenger cp = (CarriagePassenger) c;
                    if (cp.hasMoreCapacity()) carriagePassenger = cp;
                }
                if (c instanceof CarriageLoad && carriageLoad == null) {
                    CarriageLoad cl = (CarriageLoad) c;
                    if (cl.hasMoreCapacity()) carriageLoad = cl;
                }
            }

            if (carriagePassenger == null) {
                log("No hay vagones de pasajeros con capacidad disponible en este tren.");
                return null;
            }

            // Calcular precio según categoría y distancia
            double distancia = route.getTotalDistance();
            double tarifaBase;
            switch (category) {
                case 0: tarifaBase = 150; break;  // Premium
                case 1: tarifaBase = 100; break;  // Ejecutivo
                default: tarifaBase = 60; break;  // Estándar
            }
            int precio = (int) Math.round(distancia * tarifaBase);

            // Generar ID de tiquete automáticamente
            String ticketId;
            try {
                int count = ticketService.getTickets().size();
                ticketId = String.format("TKT-%04d", count + 1);
            } catch (Exception e) {
                ticketId = "TKT-" + System.currentTimeMillis();
            }

            // Crear tiquete
            Ticket ticket = new Ticket(
                    ticketId,
                    currentPassenger,
                    route,
                    train,
                    carriageLoad,
                    carriagePassenger,
                    category,
                    true,
                    java.time.LocalDate.now().toString()
            );
            ticket.setPrice(precio);

            // Registrar pasajero en el vagón
            carriagePassenger.addPassenger(ticket);

            // Agregar maletas SOLO si hay vagón de carga disponible
            if (maletas != null && !maletas.isEmpty()) {
                if (carriageLoad == null) {
                    log("Advertencia: este tren no tiene vagón de carga. Las maletas no se registraron.");
                } else {
                    for (Luggage l : maletas) {
                        boolean ok = ticket.addLuggage(l);
                        if (!ok) {
                            log("Advertencia: maleta de " + l.getWeight()
                                    + " kg no se pudo agregar (vagón lleno o peso excedido).");
                        }
                    }
                }
            }

            Ticket registered = ticketService.register(ticket);
            log("Ticket comprado! ID: " + registered.getId()
                    + " | Ruta: " + registered.getRoute().getName()
                    + " | Categoría: " + registered.getCategory()
                    + " | Precio: $" + precio);
            return registered;

        } catch (Exception e) {
            log("Error al comprar un ticket: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /** Calcula el precio estimado de un tiquete según categoría y distancia de la ruta */
    public int calcularPrecio(Route route, int category) {
        double distancia = route.getTotalDistance();
        double tarifaBase;
        switch (category) {
            case 0: tarifaBase = 150; break;  // Premium
            case 1: tarifaBase = 100; break;  // Ejecutivo
            default: tarifaBase = 60; break;  // Estándar
        }
        return (int) Math.round(distancia * tarifaBase);
    }

    public Passenger getCurrentPassenger() { return currentPassenger; }
    public String getLogger()              { return logger; }
    public boolean isLoggedIn()            { return currentPassenger != null; }

    public void logout() {
        currentPassenger = null;
        log("Sesión cerrada.");
    }

    private void log(String msg) {
        this.logger = msg;
        this.notifyObservers();
    }
}
