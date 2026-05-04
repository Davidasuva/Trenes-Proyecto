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

/**
 * Modelo principal de la aplicación cliente de compra de tickets.
 * <p>
 * Extiende {@link Subject} para implementar el patrón Observador: cuando
 * ocurre un evento (login, compra, error), notifica a todos los observadores
 * registrados con el mensaje de log actualizado.
 * </p>
 *
 * <p>Se conecta al servidor de trenes vía RMI para gestionar:</p>
 * <ul>
 *   <li>Autenticación y registro de pasajeros.</li>
 *   <li>Consulta de rutas disponibles.</li>
 *   <li>Compra de tickets con equipaje opcional.</li>
 *   <li>Historial de tickets del pasajero.</li>
 *   <li>Cambio de ruta de un ticket activo.</li>
 * </ul>
 *
 * <p><b>Precios por categoría:</b></p>
 * <ul>
 *   <li>Premium (0): $150 COP × km</li>
 *   <li>Ejecutivo (1): $100 COP × km</li>
 *   <li>Estándar (2): $60 COP × km</li>
 * </ul>
 *
 * @author Equipo Dinamita
 * @version 1.0.0
 * @see Passenger
 * @see Ticket
 * @see Route
 */
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

    /**
     * Construye el modelo con los datos de conexión al servidor RMI.
     *
     * @param ip          IP del servidor
     * @param port        puerto RMI
     * @param serviceName nombre base del servicio en el registro RMI
     */
    public ClientModel(String ip, int port, String serviceName) {
        this.uri         = "//" + ip + ":" + port + "/" + serviceName;
        this.userUri     = "//" + ip + ":" + port + "/" + serviceName + "-users";
        this.routeUri    = "//" + ip + ":" + port + "/" + serviceName + "-routes";
        this.carriageUri = "//" + ip + ":" + port + "/" + serviceName + "-carriages";
    }

    /**
     * Establece la conexión inicial con los cuatro servicios remotos del servidor.
     *
     * @return {@code true} si todos los servicios se conectaron correctamente
     */
    public boolean connect() {
        try {
            ticketService   = (TicketInterface)   Naming.lookup(uri);
            userService     = (User)              Naming.lookup(userUri);
            routeService    = (RouteInterface)    Naming.lookup(routeUri);
            carriageService = (CarriageInterface) Naming.lookup(carriageUri);
            log("Conectando al servidor: " + uri);
            return true;
        } catch (Exception e) {
            log("No se pudo conectar a: " + uri + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Autentica a un pasajero existente con correo y contraseña.
     *
     * @param email    correo electrónico registrado
     * @param password contraseña
     * @return {@code true} si las credenciales son válidas y el usuario es un Pasajero
     */
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
            return false;
        }
    }

    /**
     * Registra un nuevo pasajero en el sistema.
     *
     * @param id                 número de documento
     * @param mail               correo electrónico
     * @param name               nombre
     * @param lastName           apellido
     * @param password           contraseña
     * @param typeIdentification tipo de documento
     * @param address            dirección
     * @return {@code true} si el registro fue exitoso
     */
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
            return false;
        }
    }

    /**
     * Obtiene la lista de rutas disponibles para compra.
     *
     * @return lista de rutas activas; lista vacía si hay error
     */
    public LinkedList<Route> getAvailableRoutes() {
        try {
            LinkedList<Route> routes = routeService.getAvailableRoutes();
            log("Se encontraron: " + routes.size() + " rutas disponibles");
            return routes;
        } catch (Exception e) {
            log("Error al obtener rutas: " + e.getMessage());
            return new LinkedList<>();
        }
    }

    /**
     * Compra un ticket sin maletas (sobrecarga por compatibilidad).
     *
     * @param route    ruta del viaje
     * @param category categoría del asiento (0=Premium, 1=Ejecutivo, 2=Estándar)
     * @return ticket comprado, o {@code null} si falló
     */
    public Ticket buyTicket(Route route, int category) {
        return buyTicket(route, category, null);
    }

    /**
     * Compra un ticket con lista opcional de maletas.
     * <p>El proceso:
     * <ol>
     *   <li>Verifica que el pasajero esté autenticado.</li>
     *   <li>Busca un vagón de pasajeros con cupo disponible.</li>
     *   <li>Si hay maletas, busca un vagón de carga con espacio suficiente.</li>
     *   <li>Calcula el precio según categoría y distancia.</li>
     *   <li>Registra el ticket en el servidor vía RMI.</li>
     *   <li>Registra las maletas en el vagón de carga del servidor.</li>
     * </ol>
     * </p>
     *
     * @param route    ruta del viaje
     * @param category categoría del asiento (0, 1 o 2)
     * @param maletas  lista de maletas a registrar, puede ser {@code null}
     * @return ticket registrado, o {@code null} si no hay cupo o ocurrió un error
     */
    public Ticket buyTicket(Route route, int category, LinkedList<Luggage> maletas) {
        if (currentPassenger == null) {
            log("Debe iniciar sesión para poder comprar un ticket.");
            return null;
        }
        try {
            server.model.train.Train train = route.getTrains().peek();
            if (train == null) { log("La ruta no tiene un tren asignado."); return null; }

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
                    if (cl.getLuggageCount() + maletasARegistrar <= CarriageLoad.MAX_LUGGAGES_PER_WAGON) {
                        carriageLoad = cl;
                    }
                }
            }

            if (carriagePassenger == null) {
                log("No hay vagones de pasajeros con capacidad disponible en este tren.");
                return null;
            }
            if (maletasARegistrar > 0 && carriageLoad == null) {
                log("No hay espacio en los vagones de carga para " + maletasARegistrar + " maleta(s).");
                return null;
            }

            double distancia = route.getTotalDistance();
            double tarifaBase = (category == 0) ? 150 : (category == 1) ? 100 : 60;
            int precio = (int) Math.round(distancia * tarifaBase);

            String ticketId;
            try {
                int count = ticketService.getTickets().size();
                ticketId = String.format("TKT-%04d", count + 1);
            } catch (Exception e) {
                ticketId = "TKT-" + System.currentTimeMillis();
            }

            Ticket ticket = new Ticket(ticketId, currentPassenger, route, train,
                    carriageLoad, carriagePassenger, category, true,
                    java.time.LocalDate.now().toString());
            ticket.setPrice(precio);

            boolean agregado = carriageService.addPassengerToCarriage(carriagePassenger.getId(), ticket);
            if (!agregado) { log("No se pudo registrar el pasajero en el vagón del servidor."); return null; }

            if (maletas != null && !maletas.isEmpty() && carriageLoad != null) {
                Iterator<Luggage> iterator = maletas.iterator();
                while (iterator.hasNext()) {
                    Luggage next = iterator.next();
                    boolean ok = carriageService.addLuggageToCarriage(carriageLoad.getId(), next);
                    if (ok) { next.setCarriage(carriageLoad); ticket.getLuggage().add(next); }
                    else { log("Advertencia: maleta de " + next.getWeight() + " kg no se pudo agregar."); }
                }
            }

            Ticket registered = ticketService.register(ticket);
            log("Ticket comprado! ID: " + registered.getId() + " | Precio: $" + precio);
            return registered;

        } catch (Exception e) {
            log("Error al comprar un ticket: " + e.getMessage());
            return null;
        }
    }

    /**
     * Calcula el precio estimado de un ticket según categoría y distancia de la ruta.
     *
     * @param route    ruta del viaje
     * @param category categoría del asiento (0=Premium, 1=Ejecutivo, 2=Estándar)
     * @return precio estimado en COP
     */
    public int calcularPrecio(Route route, int category) {
        double distancia = route.getTotalDistance();
        double tarifaBase = (category == 0) ? 150 : (category == 1) ? 100 : 60;
        return (int) Math.round(distancia * tarifaBase);
    }

    /**
     * Retorna el pasajero autenticado actualmente.
     *
     * @return pasajero autenticado, o {@code null} si no hay sesión activa
     */
    public Passenger getCurrentPassenger() { return currentPassenger; }

    /** @return último mensaje de log del modelo */
    public String getLogger() { return logger; }

    /** @return {@code true} si hay un pasajero autenticado */
    public boolean isLoggedIn() { return currentPassenger != null; }

    /**
     * Obtiene todos los tickets del pasajero autenticado.
     *
     * @return lista de tickets; lista vacía si no hay sesión o hubo error
     */
    public LinkedList<Ticket> getMyTickets() {
        if (currentPassenger == null) return new LinkedList<>();
        try {
            return ticketService.seeTicketsPerPassenger(currentPassenger);
        } catch (Exception e) {
            log("Error al obtener historial: " + e.getMessage());
            return new LinkedList<>();
        }
    }

    /**
     * Obtiene rutas con el mismo origen que una ruta dada (útil para cambio de destino).
     *
     * @param current ruta de referencia
     * @return lista de rutas con el mismo origen pero distinto ID
     */
    public LinkedList<Route> getRoutesWithSameOrigin(Route current) {
        try {
            LinkedList<Route> all = routeService.getAvailableRoutes();
            LinkedList<Route> result = new LinkedList<>();
            Iterator<Route> it = all.iterator();
            while (it.hasNext()) {
                Route r = it.next();
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

    /**
     * Cambia la ruta de un ticket activo y persiste el cambio en el servidor vía RMI.
     *
     * @param ticket   ticket cuya ruta se desea cambiar
     * @param newRoute nueva ruta a asignar
     * @return {@code true} si el cambio fue exitoso
     */
    public boolean changeTicketRoute(Ticket ticket, Route newRoute) {
        try {
            ticket.setRoute(newRoute);
            ticketService.modifyTicket(ticket, ticket.getId());
            log("Ruta actualizada a: " + newRoute.getName());
            return true;
        } catch (Exception e) {
            log("Error cambiando ruta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cierra la sesión del pasajero actual.
     */
    public void logout() {
        currentPassenger = null;
        log("Sesión cerrada.");
    }

    private void log(String msg) {
        this.logger = msg;
        this.notifyObservers();
    }
}
