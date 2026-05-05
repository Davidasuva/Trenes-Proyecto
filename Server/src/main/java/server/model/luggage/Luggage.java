package server.model.luggage;
import server.model.carriage.CarriageLoad;
import server.model.ticket.Ticket;
import java.io.Serializable;

/**
 * Representa una maleta de equipaje registrada en el sistema.
 * <p>
 * Cada maleta tiene un peso máximo permitido de 80 kg y se almacena en un
 * {@link CarriageLoad}. Una maleta puede estar asociada a un {@link Ticket}
 * específico.
 * </p>
 *
 * @author Equipo Dinamita
 * @version 1.0.0
 * @see CarriageLoad
 * @see Ticket
 */
public class Luggage implements Serializable, Comparable<Luggage> {
    private static final long serialVersionUID = 1L;
    private int id;
    private int weight;
    private CarriageLoad carriage;
    private Ticket ticket;

    /**
     * Construye una maleta con su identificador y peso.
     *
     * @param id     identificador de la maleta
     * @param weight peso de la maleta en kilogramos (máximo 80 kg)
     */
    public Luggage(int id, int weight) {
        this.id = id;
        this.weight = weight;
    }

    /** @return identificador de la maleta */
    public int getId() { return id; }

    /** @return peso de la maleta en kg */
    public int getWeight() { return weight; }

    /** @return vagón de carga donde está almacenada la maleta */
    public CarriageLoad getCarriage() { return carriage; }

    /** @return ticket al que pertenece la maleta */
    public Ticket getTicket() { return ticket; }

    /** @param weight nuevo peso en kg */
    public void setWeight(int weight) { this.weight = weight; }

    /** @param carriage vagón de carga asignado */
    public void setCarriage(CarriageLoad carriage) { this.carriage = carriage; }

    /** @param ticket ticket al que pertenece esta maleta */
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    /**
     * Compara dos maletas por su ID.
     *
     * @param o objeto a comparar
     * @return {@code true} si ambas maletas tienen el mismo ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Luggage)) return false;
        Luggage l = (Luggage) o;
        return this.id == l.getId();
    }

    @Override
    public int compareTo(Luggage l) {
        return Integer.compare(this.id, l.getId());
    }
}
