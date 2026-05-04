package server.model.user;

import edu.uva.app.array.Array;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import server.model.carriage.AbstractCarriage;
import server.model.luggage.Luggage;
import server.model.ticket.Ticket;

/**
 * Representa a un pasajero registrado en el sistema de trenes.
 * <p>
 * Extiende {@link AbstractUser} con tipo 1, y añade funcionalidades
 * específicas: historial de tickets, ticket activo en curso y estado de viaje.
 * Un pasajero puede tener múltiples tickets históricos pero solo un
 * {@code actualTicket} que representa el viaje activo en curso.
 * </p>
 *
 * @author Equipo ValidarTicket
 * @version 1.0
 * @see AbstractUser
 * @see Ticket
 */
public class Passenger extends AbstractUser {

    /** Historial completo de todos los tickets del pasajero. */
    LinkedList<Ticket> historyTickets;

    /** Ticket activo del viaje en curso. Puede ser {@code null} si no está viajando. */
    Ticket actualTicket;

    /** Indica si el pasajero está actualmente en viaje. */
    Boolean isTraveling;

    /**
     * Construye un pasajero con sus datos personales básicos.
     * Inicializa el historial de tickets vacío y el estado como no viajando.
     *
     * @param id                 número de documento del pasajero
     * @param mail               correo electrónico
     * @param name               nombre de pila
     * @param lastName           apellido
     * @param password           contraseña
     * @param typeIdetification  tipo de documento (ej. "C.C")
     * @param adress             dirección de residencia
     */
    public Passenger(String id, String mail, String name, String lastName,
                     String password, String typeIdetification, String adress) {
        super(id, mail, name, lastName, password, typeIdetification, adress, 1);
        actualTicket = null;
        isTraveling = false;
        historyTickets = new LinkedList<>();
    }

    /**
     * Agrega un ticket al historial del pasajero.
     *
     * @param ticket ticket a registrar en el historial
     * @return {@code true} si se agregó correctamente, {@code false} en caso de error
     */
    public boolean addTicket(Ticket ticket) {
        try {
            historyTickets.add(ticket);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retorna el ticket del viaje activo del pasajero.
     *
     * @return ticket activo, o {@code null} si el pasajero no está en viaje
     */
    public Ticket getActualTicket() { return actualTicket; }

    /**
     * Establece el ticket activo del pasajero para el viaje en curso.
     *
     * @param actualTicket ticket que representa el viaje activo
     */
    public void setActualTicket(Ticket actualTicket) { this.actualTicket = actualTicket; }

    /**
     * Sincroniza el estado {@code isTraveling} con el estado del ticket activo.
     * Si el pasajero no tiene un ticket activo asignado, no se realiza ningún cambio.
     */
    public void setTraveling() {
        if (actualTicket != null) {
            this.isTraveling = actualTicket.Status();
        }
    }

    /**
     * Indica si el pasajero está actualmente en viaje.
     *
     * @return {@code true} si el pasajero tiene un ticket activo con estado en curso
     */
    public boolean IsTraveling() { return isTraveling; }

    /**
     * Retorna el equipaje del pasajero desde su ticket activo.
     *
     * @return arreglo de maletas del ticket activo
     * @throws NullPointerException si {@code actualTicket} es {@code null}
     */
    public Array<Luggage> getLuggage() { return actualTicket.getLuggage(); }

    /**
     * Retorna el vagón asignado al pasajero en su ticket activo.
     *
     * @return vagón de pasajeros asignado
     * @throws NullPointerException si {@code actualTicket} es {@code null}
     */
    public AbstractCarriage getCarriage() { return actualTicket.getCarriagePassenger(); }
}
