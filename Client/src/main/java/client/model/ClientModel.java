package client.model;

import java.rmi.Naming;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import server.model.carriage.AbstractCarriage;
import server.model.carriage.CarriageInterface;
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
    private final String carriageUri;

    private TicketInterface ticketService;
    private User userService;
    private RouteInterface routeService;
    private CarriageInterface carriageService;

    private String logger;
    private Passenger currentPassenger;

    public ClientModel(String ip, int port, String serviceName) {
        this.uri      = "//" + ip + ":" + port + "/" + serviceName;
        this.userUri  = "//" + ip + ":" + port + "/" + serviceName + "-users";
        this.routeUri = "//" + ip + ":" + port + "/" + serviceName + "-routes";
        this.carriageUri = "//" + ip + ":" + port + "/" + serviceName + "-carriages";
    }

    public boolean connect() {
        try {
            ticketService = (TicketInterface) Naming.lookup(uri);
            userService   = (User)            Naming.lookup(userUri);
            routeService  = (RouteInterface)  Naming.lookup(routeUri);
            carriageService = (CarriageInterface) Naming.lookup(carriageUri);
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
    public Ticket buyTicket(Route route, int category, LinkedList<Luggage> maletas) {
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

            int maletasARegistrar = (maletas != null) ? maletas.size() : 0;

            Iterator<AbstractCarriage> itC = train.getCarriages().iterator();
            while (itC.hasNext()) {
                AbstractCarriage c = itC.next();
                if (c instanceof CarriagePassenger && carriagePassenger == null) {
                    CarriagePassenger cp = (CarriagePassenger) c;
                    if (cp.hasMoreCapacity()) carriagePassenger = cp;
                }
                if (c instanceof CarriageLoad && carriageLoad == null) {
                    CarriageLoad cl = (CarriageLoad) c;
                    // El vagón debe tener espacio para TODAS las maletas del pasajero
                    if (cl.getLuggageCount() + maletasARegistrar <= server.model.carriage.CarriageLoad.MAX_LUGGAGES_PER_WAGON) {
                        carriageLoad = cl;
                    }
                }
            }

            if (carriagePassenger == null) {
                log("No hay vagones de pasajeros con capacidad disponible en este tren.");
                return null;
            }

            // Si quiere llevar maletas pero no hay vagón de carga con espacio, bloquear compra
            if (maletasARegistrar > 0 && carriageLoad == null) {
                log("No hay espacio en los vagones de carga para " + maletasARegistrar
                        + " maleta(s). Cada vagón admite máximo "
                        + server.model.carriage.CarriageLoad.MAX_LUGGAGES_PER_WAGON
                        + " maletas. Por favor, reduce la cantidad de maletas o no las registres.");
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

            // Registrar pasajero en el vagón del SERVIDOR vía RMI
            boolean agregado = carriageService.addPassengerToCarriage(carriagePassenger.getId(), ticket);
            if (!agregado) {
                log("No se pudo registrar el pasajero en el vagón del servidor.");
                return null;
            }

            // Agregar maletas SOLO si hay vagón de carga disponible
            if (maletas != null && !maletas.isEmpty()) {
                if (carriageLoad == null) {
                    log("Advertencia: este tren no tiene vagón de carga. Las maletas no se registraron.");
                } else {
                    Iterator<Luggage> iterator=maletas.iterator();
                    while(iterator.hasNext()) {
                        Luggage next=iterator.next();
                        boolean ok=carriageService.addLuggageToCarriage(carriageLoad.getId(), next);
                        if(ok){
                            next.setCarriage(carriageLoad);
                            ticket.getLuggage().add(next); // registrar en el ticket para mostrarlo
                        } else {
                            log("Advertencia: maleta de " + next.getWeight()
                                    + " kg no se pudo agregar (vagón lleno o peso excedido).");
                        }
                    }
                }
            }

            Ticket registered = ticketService.register(ticket);

            // Refrescar vagones desde el servidor para mostrar contadores reales
            try {
                AbstractCarriage cpActualizado = carriageService.getCarriageById(carriagePassenger.getId());
                if (cpActualizado instanceof CarriagePassenger) {
                    registered.setCarriagePassenger((CarriagePassenger) cpActualizado);
                }
                if (carriageLoad != null) {
                    AbstractCarriage clActualizado = carriageService.getCarriageById(carriageLoad.getId());
                    if (clActualizado instanceof CarriageLoad) {
                        registered.setCarriageLoad((CarriageLoad) clActualizado);
                    }
                }
            } catch (Exception ignored) {}

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

    /** Obtiene todos los tickets del pasajero actual (activos e históricos) */
    public LinkedList<server.model.ticket.Ticket> getMyTickets() {
        if (currentPassenger == null) return new LinkedList<>();
        try {
            return ticketService.seeTicketsPerPassenger(currentPassenger);
        } catch (Exception e) {
            log("Error al obtener historial: " + e.getMessage());
            return new LinkedList<>();
        }
    }

    /** Rutas con el mismo origen que la ruta actual (para cambio de destino) */
    public LinkedList<server.model.route.Route> getRoutesWithSameOrigin(server.model.route.Route current) {
        try {
            LinkedList<server.model.route.Route> all = routeService.getAvailableRoutes();
            LinkedList<server.model.route.Route> result = new LinkedList<>();
            Iterator<server.model.route.Route> it = all.iterator();
            while (it.hasNext()) {
                server.model.route.Route r = it.next();
                if (r.getOrigin().equals(current.getOrigin()) && r.getId() != current.getId()) {
                    result.add(r);
                }
            }
            return result;
        } catch (Exception e) {
            log("Error obteniendo rutas: " + e.getMessage());
            return new LinkedList<>();
        }
    }

    /** Cambia la ruta de un ticket activo y persiste el cambio en el servidor */
    public boolean changeTicketRoute(server.model.ticket.Ticket ticket, server.model.route.Route newRoute) {
        try {
            // Aplicar el cambio localmente para que la UI refleje el valor nuevo
            ticket.setRoute(newRoute);
            // Persistir en el servidor usando el método remoto modifyTicket
            ticketService.modifyTicket(ticket, ticket.getId());
            log("Ruta actualizada a: " + newRoute.getName());
            return true;
        } catch (Exception e) {
            log("Error cambiando ruta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void logout() {
        currentPassenger = null;
        log("Sesión cerrada.");
    }

    private void log(String msg) {
        this.logger = msg;
        this.notifyObservers();
    }
}
