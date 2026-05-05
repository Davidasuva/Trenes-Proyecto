package server.model.carriage;
import java.io.Serializable;

/**
 * Clase base abstracta para todos los tipos de vagones del tren.
 * <p>
 * Define el identificador único común a vagones de pasajeros ({@link CarriagePassenger})
 * y de carga ({@link CarriageLoad}). Implementa {@link Serializable} para la
 * transferencia vía RMI.
 * </p>
 *
 * @author Equipo Dinamita
 * @version 1.0.0
 * @see CarriagePassenger
 * @see CarriageLoad
 */
public abstract class AbstractCarriage implements Serializable, Comparable<AbstractCarriage> {
    private static final long serialVersionUID = 4L;

    private int id;

    /**
     * Construye un vagón con el identificador dado.
     *
     * @param id identificador único del vagón dentro del tren
     */
    public AbstractCarriage(int id) { this.id = id; }

    /** @return identificador único del vagón */
    public int getId() { return id; }

    /**
     * Compara dos vagones por su ID numérico.
     *
     * @param o objeto a comparar
     * @return {@code true} si ambos vagones tienen el mismo ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractCarriage)) return false;
        AbstractCarriage c = (AbstractCarriage) o;
        return this.id == c.getId();
    }

    @Override
    public int compareTo(AbstractCarriage c) {
        return Integer.compare(this.id, c.getId());
    }
}
