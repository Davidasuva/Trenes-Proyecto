package server.model.train;

import edu.uva.app.linkedlist.singly.singly.LinkedList;
import server.model.carriage.AbstractCarriage;
import java.io.Serializable;

/**
 * Representa un tren del sistema con su configuración de vagones.
 * <p>
 * Un tren tiene una capacidad declarada de vagones de pasajeros y de carga.
 * Los vagones reales se gestionan mediante una lista enlazada de
 * {@link AbstractCarriage}, que puede contener tanto {@code CarriagePassenger}
 * como {@code CarriageLoad}.
 * </p>
 *
 * @author Equipo Dinamita
 * @version 1.0.0
 * @see AbstractCarriage
 */
public class Train implements Serializable {
    private static final long serialVersionUID = 3L;

    private int id;
    private String name;
    private String type;
    /** Número de vagones de pasajeros */
    private int capacity;
    /** Número de vagones de carga */
    private int cargoWagons;
    private int mileage;
    private LinkedList<AbstractCarriage> carriages;

    /**
     * Construye un tren con su configuración inicial.
     * La lista de vagones empieza vacía; se deben agregar con {@link #addCarriage}.
     *
     * @param id          identificador único del tren
     * @param name        nombre del tren
     * @param type        tipo de tren (ej. "Expreso", "Regional")
     * @param capacity    número de vagones de pasajeros
     * @param cargoWagons número de vagones de carga
     * @param mileage     kilometraje inicial del tren
     */
    public Train(int id, String name, String type, int capacity, int cargoWagons, int mileage) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.cargoWagons = cargoWagons;
        this.mileage = mileage;
        carriages = new LinkedList<>();
    }

    /**
     * Agrega un vagón al tren.
     *
     * @param carriage vagón a agregar (de pasajeros o de carga)
     * @return {@code true} si se agregó exitosamente
     */
    public boolean addCarriage(AbstractCarriage carriage) { return carriages.add(carriage); }

    /**
     * Elimina un vagón del tren.
     *
     * @param carriage vagón a eliminar
     * @return {@code true} si se eliminó exitosamente
     */
    public boolean removeCarriage(AbstractCarriage carriage) { return carriages.remove(carriage); }

    /** @return ID único del tren */
    public int getId() { return id; }

    /** @return nombre del tren */
    public String getName() { return name; }

    /** @param name nuevo nombre */
    public void setName(String name) { this.name = name; }

    /** @return tipo de tren */
    public String getType() { return type; }

    /** @param type nuevo tipo */
    public void setType(String type) { this.type = type; }

    /** @return número de vagones de pasajeros */
    public int getCapacity() { return capacity; }

    /** @param capacity nuevo número de vagones de pasajeros */
    public void setCapacity(int capacity) { this.capacity = capacity; }

    /** @return kilometraje actual del tren */
    public int getMileage() { return mileage; }

    /**
     * Suma kilómetros adicionales al kilometraje del tren.
     *
     * @param mileage kilómetros a sumar
     */
    public void updateMileage(int mileage) { this.mileage += mileage; }

    /** @return número de vagones de carga */
    public int getCargoWagons() { return cargoWagons; }

    /** @param cargoWagons nuevo número de vagones de carga */
    public void setCargoWagons(int cargoWagons) { this.cargoWagons = cargoWagons; }

    /** @return lista de vagones del tren */
    public LinkedList<AbstractCarriage> getCarriages() { return carriages; }

    /**
     * Compara dos trenes por su ID numérico.
     *
     * @param obj objeto a comparar
     * @return {@code true} si los trenes tienen el mismo ID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Train)) return false;
        Train t = (Train) obj;
        return this.id == t.getId();
    }
}
