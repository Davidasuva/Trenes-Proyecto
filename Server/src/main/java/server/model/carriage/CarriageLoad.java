package server.model.carriage;

import server.model.luggage.Luggage;
import edu.uva.app.stack.array.Stack;

/**
 * Vagón de carga de un tren para almacenar el equipaje de los pasajeros.
 * <p>
 * Las maletas se almacenan en una pila (LIFO). El vagón tiene dos restricciones:
 * <ul>
 *   <li>Máximo {@link #MAX_LUGGAGES_PER_WAGON} maletas por vagón.</li>
 *   <li>Cada maleta no puede superar 80 kg.</li>
 * </ul>
 * </p>
 *
 * @author Equipo ValidarTicket
 * @version 1.0
 * @see AbstractCarriage
 * @see Luggage
 */
public class CarriageLoad extends AbstractCarriage {

    private Stack<Luggage> luggages;
    private int maxCapacity;
    private int actualWeight;
    private int luggageCount;

    /** Número máximo de maletas permitidas por vagón de carga. */
    public static final int MAX_LUGGAGES_PER_WAGON = 2;

    /**
     * Construye un vagón de carga con su capacidad de peso máxima.
     *
     * @param id          identificador único del vagón
     * @param maxCapacity peso máximo total permitido en kilogramos
     */
    public CarriageLoad(int id, int maxCapacity) {
        super(id);
        this.maxCapacity = maxCapacity;
        luggages = new Stack<>(MAX_LUGGAGES_PER_WAGON);
        luggageCount = 0;
    }

    /** @return peso total actual del equipaje almacenado en kg */
    public int getActualWeight() { return actualWeight; }

    /** @return número de maletas actualmente en el vagón */
    public int getLuggageCount() { return luggageCount; }

    /**
     * Intenta agregar una maleta al vagón de carga.
     * <p>Rechaza la maleta si supera 80 kg o si el vagón ya tiene
     * {@link #MAX_LUGGAGES_PER_WAGON} maletas.</p>
     *
     * @param luggage maleta a agregar
     * @return {@code true} si se agregó correctamente
     */
    public boolean addLuggage(Luggage luggage) {
        if (luggage.getWeight() > 80) return false;
        if (luggageCount >= MAX_LUGGAGES_PER_WAGON) return false;
        actualWeight += luggage.getWeight();
        luggage.setCarriage(this);
        boolean pushed = luggages.push(luggage);
        if (pushed) luggageCount++;
        return pushed;
    }

    /**
     * Indica si el vagón puede recibir más maletas.
     *
     * @return {@code true} si aún hay espacio para al menos una maleta más
     */
    public boolean hasMoreCapacity() { return luggageCount < MAX_LUGGAGES_PER_WAGON; }

    /** @return pila de maletas almacenadas */
    public Stack<Luggage> getLuggages() { return luggages; }

    /** @return capacidad de peso máxima del vagón en kg */
    public int getMaxCapacity() { return maxCapacity; }
}
