package boardingScreen.model;

import edu.uva.app.linkedlist.singly.singly.LinkedList;
import server.model.observer.Subject;
import server.model.route.Route;
import server.model.route.RouteInterface;
import server.model.ticket.TicketInterface;
import server.model.user.User;

import java.rmi.Naming;

/**
 * Modelo de la pantalla de abordaje (PantallaAbordaje).
 * <p>
 * Extiende {@link Subject} para el patrón Observador. Gestiona la conexión
 * RMI con el servidor y provee los datos necesarios para mostrar el orden
 * de abordaje de una ruta seleccionada.
 * </p>
 *
 * <p>Flujo de uso:</p>
 * <ol>
 *   <li>Instanciar con IP, puerto y nombre del servicio.</li>
 *   <li>Llamar {@link #connect()} para establecer conexión.</li>
 *   <li>Llamar {@link #getAvailableRoutes()} para que el operador seleccione una ruta.</li>
 *   <li>Guardar la ruta con {@link #setCurrentRoute(Route)}.</li>
 *   <li>Llamar {@link #getBoardingOrder(int)} para obtener el orden de abordaje.</li>
 * </ol>
 *
 * @author Equipo Dinamita
 * @version 1.0.0
 * @see Route
 */
public class BoardingScreenModel extends Subject {
    private final String uri;
    private final String userUri;
    private final String routeUri;

    private TicketInterface ticketService;
    private User userService;
    private RouteInterface routeService;

    private String logger;
    private Route currentRoute;

    /**
     * Construye el modelo con los datos de conexión al servidor RMI.
     *
     * @param ip          IP del servidor
     * @param port        puerto RMI
     * @param serviceName nombre base del servicio en el registro RMI
     */
    public BoardingScreenModel(String ip, int port, String serviceName) {
        this.uri      = "//" + ip + ":" + port + "/" + serviceName;
        this.userUri  = "//" + ip + ":" + port + "/" + serviceName + "-users";
        this.routeUri = "//" + ip + ":" + port + "/" + serviceName + "-routes";
    }

    /**
     * Establece la conexión con los servicios remotos del servidor.
     *
     * @return {@code true} si la conexión fue exitosa
     */
    public boolean connect() {
        try {
            ticketService = (TicketInterface) Naming.lookup(uri);
            userService   = (User)            Naming.lookup(userUri);
            routeService  = (RouteInterface)  Naming.lookup(routeUri);
            log("Conectado al servidor: " + uri);
            return true;
        } catch (Exception e) {
            log("No se pudo conectar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene la lista de rutas disponibles para abordaje.
     *
     * @return lista de rutas activas; lista vacía si hay error de conexión
     */
    public LinkedList<Route> getAvailableRoutes() {
        try {
            return routeService.getAvailableRoutes();
        } catch (Exception e) {
            log("Error obteniendo rutas: " + e.getMessage());
            return new LinkedList<>();
        }
    }

    /**
     * Obtiene el orden de abordaje de una ruta como lista de líneas de texto.
     * <p>
     * El formato de cada línea es generado por el servidor e incluye información
     * de vagón, categoría y turno de cada pasajero.
     * </p>
     *
     * @param routeId ID de la ruta cuyo orden de abordaje se desea obtener
     * @return lista de cadenas con el orden de abordaje; en caso de error,
     *         retorna una lista con el mensaje de error
     */
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

    /**
     * Establece la ruta actualmente seleccionada para mostrar su abordaje.
     *
     * @param route ruta seleccionada por el operador
     */
    public void setCurrentRoute(Route route) { this.currentRoute = route; }

    /**
     * Retorna la ruta actualmente seleccionada.
     *
     * @return ruta actual, o {@code null} si no se ha seleccionado ninguna
     */
    public Route getCurrentRoute() { return currentRoute; }

    /**
     * Retorna el último mensaje de log generado por el modelo.
     *
     * @return mensaje de log
     */
    public String getLogger() { return logger; }

    private void log(String msg) {
        this.logger = msg;
        this.notifyObservers();
    }
}
