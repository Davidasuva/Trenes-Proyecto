package server.model.ticket;

import java.io.Serializable;
import edu.uva.app.array.Array;
import server.model.user.Passenger;
import server.model.carriage.CarriageLoad;
import server.model.train.Train;
import server.model.carriage.CarriagePassenger;
import server.model.luggage.Luggage;
import server.model.route.Route;

/**
 * Representa un ticket (tiquete) de viaje en el sistema de trenes.
 * <p>
 * Un ticket vincula a un {@link Passenger} con una {@link Route} específica,
 * un {@link Train}, y los vagones asignados ({@link CarriagePassenger} y
 * {@link CarriageLoad}). También gestiona el equipaje permitido (máximo 2
 * maletas por ticket, cada una con un límite de 80 kg).
 * </p>
 *
 * <p><b>Categorías de ticket:</b></p>
 * <ul>
 *   <li>0 → Premium</li>
 *   <li>1 → Ejecutivo</li>
 *   <li>2 → Estándar</li>
 * </ul>
 *
 * @author Equipo Dinamita
 * @version 1.0.0
 * @see Passenger
 * @see Route
 * @see Luggage
 */
public class Ticket implements Serializable, Comparable<Ticket> {
    private static final long serialVersionUID = 1L;

    private String id;
    private Passenger passenger;
    private Route route;
    private Train train;
    private CarriagePassenger carriagePassenger;
    private CarriageLoad carriageLoad;
    /** 0=Premium, 1=Ejecutivo, 2=Estándar */
    private int category;
    private int price;
    private boolean status;
    private String dateBuy;
    private Array<Luggage> luggage;
    private String contactName;
    private String contactLastName;
    private String contactPhone;

    /**
     * Construye un ticket con todos sus atributos principales.
     * Automáticamente agrega este ticket al historial del pasajero.
     *
     * @param id                 identificador único del ticket (ej. "TKT-0001")
     * @param passenger          pasajero dueño del ticket
     * @param route              ruta del viaje
     * @param train              tren asignado a la ruta
     * @param carriageLoad       vagón de carga asignado para el equipaje
     * @param carriagePassenger  vagón de pasajeros asignado
     * @param category           categoría del asiento (0=Premium, 1=Ejecutivo, 2=Estándar)
     * @param status             {@code true} si el ticket está activo
     * @param dateBuy            fecha de compra en formato texto
     */
    public Ticket(String id, Passenger passenger, Route route, Train train,
                  CarriageLoad carriageLoad, CarriagePassenger carriagePassenger,
                  int category, boolean status, String dateBuy) {
        this.id = id;
        this.passenger = passenger;
        this.route = route;
        this.train = train;
        this.carriagePassenger = carriagePassenger;
        this.carriageLoad = carriageLoad;
        this.category = category;
        this.status = status;
        this.dateBuy = dateBuy;
        luggage = new Array<>(2);
        passenger.addTicket(this);
    }

    /**
     * Verifica si una maleta cumple el límite de peso permitido (máximo 80 kg).
     *
     * @param luggage maleta a verificar
     * @return {@code true} si la maleta pesa 80 kg o menos
     */
    public boolean verificateLuggage(Luggage luggage) {
        return luggage.getWeight() <= 80;
    }

    /**
     * Intenta agregar una maleta al ticket.
     * <p>Solo se permiten máximo 2 maletas por ticket. Además, la maleta debe
     * pasar la verificación de peso y caber en el vagón de carga asignado.</p>
     *
     * @param luggage maleta a agregar
     * @return {@code true} si se agregó correctamente; {@code false} si el ticket
     *         ya tiene 2 maletas, la maleta supera 80 kg, o el vagón no tiene espacio
     */
    public boolean addLuggage(Luggage luggage) {
        if (this.luggage.size() < 2) {
            if (verificateLuggage(luggage) && carriageLoad.addLuggage(luggage)) {
                return this.luggage.add(luggage);
            }
        }
        return false;
    }

    /**
     * Retorna el identificador único del ticket.
     *
     * @return ID del ticket (ej. "TKT-0001")
     */
    public String getId() { return id; }

    /**
     * Retorna la ruta del viaje asociada al ticket.
     *
     * @return ruta del ticket
     */
    public Route getRoute() { return route; }

    /**
     * Retorna el tren asignado a la ruta del ticket.
     *
     * @return tren del ticket
     */
    public Train getTrain() { return train; }

    /**
     * Retorna el vagón de pasajeros asignado al ticket.
     *
     * @return vagón de pasajeros
     */
    public CarriagePassenger getCarriagePassenger() { return carriagePassenger; }

    /**
     * Retorna el vagón de carga asignado para el equipaje del ticket.
     *
     * @return vagón de carga
     */
    public CarriageLoad getCarriageLoad() { return carriageLoad; }

    /**
     * Retorna la categoría del ticket.
     *
     * @return 0 para Premium, 1 para Ejecutivo, 2 para Estándar
     */
    public int getCategory() { return category; }

    /**
     * Retorna el precio del ticket en pesos colombianos.
     *
     * @return precio en COP
     */
    public int getPrice() { return price; }

    /**
     * Indica si el ticket está activo (válido para abordar).
     *
     * @return {@code true} si el ticket está activo
     */
    public boolean Status() {
        return status;
    }

    /**
     * Retorna la fecha de compra del ticket.
     *
     * @return fecha de compra como texto
     */
    public String getDateBuy() { return dateBuy; }

    /**
     * Retorna el equipaje registrado en el ticket.
     *
     * @return arreglo de maletas (máximo 2)
     */
    public Array<Luggage> getLuggage() { return luggage; }

    /**
     * Actualiza la ruta del ticket (usado en cambios de ruta).
     *
     * @param route nueva ruta
     */
    public void setRoute(Route route) { this.route = route; }

    /**
     * Actualiza el tren del ticket.
     *
     * @param train nuevo tren
     */
    public void setTrain(Train train) { this.train = train; }

    /**
     * Actualiza el vagón de pasajeros del ticket.
     *
     * @param carriagePassenger nuevo vagón de pasajeros
     */
    public void setCarriagePassenger(CarriagePassenger carriagePassenger) {
        this.carriagePassenger = carriagePassenger;
    }

    /**
     * Actualiza el vagón de carga del ticket.
     *
     * @param carriageLoad nuevo vagón de carga
     */
    public void setCarriageLoad(CarriageLoad carriageLoad) {
        this.carriageLoad = carriageLoad;
    }

    /**
     * Actualiza la categoría del ticket.
     *
     * @param category nueva categoría (0=Premium, 1=Ejecutivo, 2=Estándar)
     */
    public void setCategory(int category) { this.category = category; }

    /**
     * Actualiza el estado del ticket y sincroniza el estado de viaje del pasajero.
     *
     * @param status {@code true} para activar el ticket, {@code false} para desactivarlo
     */
    public void setStatus(boolean status) {
        this.status = status;
        passenger.setTraveling();
    }

    /**
     * Establece el precio del ticket.
     *
     * @param price precio en COP
     */
    public void setPrice(int price) { this.price = price; }

    /**
     * Retorna el pasajero dueño del ticket.
     *
     * @return pasajero asociado
     */
    public Passenger getPassenger() { return passenger; }

    /**
     * Retorna el nombre de la persona de contacto de emergencia.
     *
     * @return nombre del contacto
     */
    public String getContactName() { return contactName; }

    /**
     * Establece el nombre de la persona de contacto de emergencia.
     *
     * @param contactName nombre del contacto
     */
    public void setContactName(String contactName) { this.contactName = contactName; }

    /**
     * Retorna el apellido de la persona de contacto de emergencia.
     *
     * @return apellido del contacto
     */
    public String getContactLastName() { return contactLastName; }

    /**
     * Establece el apellido de la persona de contacto de emergencia.
     *
     * @param contactLastName apellido del contacto
     */
    public void setContactLastName(String contactLastName) { this.contactLastName = contactLastName; }

    /**
     * Retorna el teléfono de la persona de contacto de emergencia.
     *
     * @return teléfono del contacto
     */
    public String getContactPhone() { return contactPhone; }

    /**
     * Establece el teléfono de la persona de contacto de emergencia.
     *
     * @param contactPhone teléfono del contacto
     */
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    /**
     * Compara este ticket con otro por su ID alfanuméricamente.
     *
     * @param o ticket con el que comparar
     * @return valor negativo, cero o positivo según el orden del ID
     */
    @Override
    public int compareTo(Ticket o) { return this.id.compareTo(o.getId()); }
}
