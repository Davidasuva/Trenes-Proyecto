package server.model.carriage;

import java.io.Serializable;
import server.model.ticket.Ticket;
import edu.uva.app.priorityQueue.PriorityQueue;

/**
 * Vagón de pasajeros de un tren.
 * <p>
 * Almacena los tickets de los pasajeros en una cola de prioridad, donde
 * la prioridad se determina por la categoría del ticket
 * (0=Premium tiene mayor prioridad, 2=Estándar la menor).
 * Tiene una capacidad máxima definida al momento de creación.
 * </p>
 *
 * @author Equipo ValidarTicket
 * @version 1.0
 * @see AbstractCarriage
 * @see Ticket
 */
public class CarriagePassenger extends AbstractCarriage implements Serializable {

    private PriorityQueue<Ticket> passengers;
    private int maxCapacity;
    private int actualCapacity;

    /**
     * Construye un vagón de pasajeros con capacidad máxima definida.
     *
     * @param id          identificador único del vagón
     * @param maxCapacity número máximo de pasajeros que puede albergar
     */
    public CarriagePassenger(int id, int maxCapacity) {
        super(id);
        this.maxCapacity = maxCapacity;
        passengers = new PriorityQueue<>(3);
        actualCapacity = 0;
    }

    /**
     * Intenta agregar un pasajero al vagón usando la categoría del ticket como prioridad.
     *
     * @param passenger ticket del pasajero a agregar
     * @return {@code true} si se agregó correctamente; {@code false} si el vagón está lleno
     */
    public boolean addPassenger(Ticket passenger) {
        try {
            if (actualCapacity < maxCapacity) {
                actualCapacity++;
                return passengers.insert(passenger.getCategory(), passenger);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** @return número actual de pasajeros en el vagón */
    public int getNumberOfPassengers() { return actualCapacity; }

    /** @return cola de prioridad con los tickets de los pasajeros */
    public PriorityQueue<Ticket> getPassengers() { return passengers; }

    /** @return capacidad máxima del vagón */
    public int getMaxCapacity() { return maxCapacity; }

    /**
     * Indica si el vagón aún tiene cupos disponibles.
     *
     * @return {@code true} si la capacidad actual es menor a la máxima
     */
    public boolean hasMoreCapacity() { return actualCapacity < maxCapacity; }

    /** @return número actual de pasajeros (alias de {@link #getNumberOfPassengers()}) */
    public int getActualCapacity() { return actualCapacity; }
}
