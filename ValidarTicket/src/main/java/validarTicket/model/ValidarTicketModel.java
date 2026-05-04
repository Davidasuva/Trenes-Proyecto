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

/**
 * Modelo principal del módulo de validación de tickets para abordaje.
 * <p>
 * Se conecta al servidor de trenes mediante RMI y expone la lógica para:
 * <ul>
 *   <li>Consultar las rutas disponibles.</li>
 *   <li>Validar si un pasajero tiene un ticket activo en la ruta seleccionada.</li>
 * </ul>
 * </p>
 *
 * <p>El flujo típico de uso es:</p>
 * <ol>
 *   <li>Instanciar el modelo con IP, puerto y nombre del servicio.</li>
 *   <li>Llamar {@link #connect()} para conectarse al servidor RMI.</li>
 *   <li>Llamar {@link #getAvailableRoutes()} para listar rutas y que el usuario seleccione una.</li>
 *   <li>Asignar la ruta con {@link #setRutaSeleccionada(Route)}.</li>
 *   <li>Llamar {@link #validar(String, String, String)} para cada pasajero que aborda.</li>
 * </ol>
 *
 * @author Equipo Dinamita
 * @version 1.0.0
 * @see ResultadoValidacion
 */
public class ValidarTicketModel {

    private final String ticketUri;
    private final String userUri;
    private final String routeUri;

    private TicketInterface ticketService;
    private User userService;
    private RouteInterface routeService;

    private Route rutaSeleccionada;

    /**
     * Construye el modelo con los datos de conexión al servidor RMI.
     *
     * @param ip          dirección IP del servidor
     * @param port        puerto RMI del servidor
     * @param serviceName nombre base del servicio registrado en el registro RMI
     */
    public ValidarTicketModel(String ip, int port, String serviceName) {
        this.ticketUri = "//" + ip + ":" + port + "/" + serviceName;
        this.userUri   = "//" + ip + ":" + port + "/" + serviceName + "-users";
        this.routeUri  = "//" + ip + ":" + port + "/" + serviceName + "-routes";
    }

    /**
     * Establece la conexión con los servicios remotos del servidor.
     *
     * @return {@code true} si la conexión fue exitosa, {@code false} en caso de error
     */
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

    /**
     * Obtiene la lista de rutas activas y disponibles para abordar.
     *
     * @return lista enlazada de rutas disponibles; lista vacía si hay error de conexión
     */
    public LinkedList<Route> getAvailableRoutes() {
        try {
            return routeService.getAvailableRoutes();
        } catch (Exception e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    /**
     * Posibles resultados del proceso de validación de un pasajero.
     */
    public enum ResultadoValidacion {
        /** El pasajero no fue encontrado o las credenciales son incorrectas. */
        PASAJERO_NO_ENCONTRADO,
        /** El pasajero existe pero no tiene ticket activo en la ruta seleccionada. */
        SIN_TICKETS_EN_RUTA,
        /** El pasajero tiene un ticket activo y válido en la ruta seleccionada. */
        VALIDADO
    }

    /**
     * Valida si un pasajero puede abordar la ruta actualmente seleccionada.
     * <p>
     * La validación verifica en orden:
     * <ol>
     *   <li>Que exista un usuario con el correo y contraseña dados, y que sea {@link Passenger}.</li>
     *   <li>Que el nombre proporcionado coincida (parcialmente) con el nombre del pasajero.</li>
     *   <li>Que el pasajero tenga al menos un ticket activo ({@code status=true}) en la ruta seleccionada.</li>
     * </ol>
     * </p>
     *
     * @param nombre   nombre (o parte del nombre) del pasajero tal como aparece en su documento
     * @param correo   correo electrónico registrado del pasajero
     * @param password contraseña del pasajero
     * @return {@link ResultadoValidacion#VALIDADO} si puede abordar;
     *         {@link ResultadoValidacion#PASAJERO_NO_ENCONTRADO} si las credenciales fallan;
     *         {@link ResultadoValidacion#SIN_TICKETS_EN_RUTA} si no tiene ticket activo en la ruta
     */
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

    /**
     * Establece la ruta que se está usando para validar el abordaje.
     *
     * @param ruta ruta seleccionada por el operador
     */
    public void setRutaSeleccionada(Route ruta) { this.rutaSeleccionada = ruta; }

    /**
     * Retorna la ruta actualmente seleccionada para validación.
     *
     * @return ruta seleccionada, o {@code null} si no se ha seleccionado ninguna
     */
    public Route getRutaSeleccionada() { return rutaSeleccionada; }
}
