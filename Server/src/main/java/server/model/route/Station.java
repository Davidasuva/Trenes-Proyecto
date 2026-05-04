package server.model.route;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * Representa una estación de tren en el sistema.
 * <p>
 * Las estaciones son los nodos del {@link RouteGraph}. La igualdad entre
 * estaciones se determina por nombre, no por ID, para simplificar las
 * búsquedas en el grafo.
 * </p>
 *
 * @author Equipo ValidarTicket
 * @version 1.0
 * @see RouteGraph
 * @see Route
 */
public class Station implements Serializable {
    private static final long serialVersionUID = 5L;
    private int id;
    private String name;

    /**
     * Construye una estación con su identificador y nombre.
     *
     * @param id   identificador único de la estación
     * @param name nombre de la estación (ej. "Bucaramanga", "Bogotá")
     * @throws RemoteException si ocurre un error de comunicación remota
     */
    public Station(int id, String name) throws RemoteException {
        this.id = id;
        this.name = name;
    }

    /** @return ID único de la estación */
    public int getId() { return id; }

    /** @return nombre de la estación */
    public String getName() { return name; }

    /** @param name nuevo nombre de la estación */
    public void setName(String name) { this.name = name; }

    /**
     * Representación en texto de la estación.
     *
     * @return cadena con ID y nombre de la estación
     */
    @Override
    public String toString() {
        return "Station{id=" + id + ", name=" + name + '}';
    }

    /**
     * Compara dos estaciones por nombre (sin distinguir mayúsculas implícitamente
     * ya que el nombre se almacena tal cual).
     *
     * @param obj objeto a comparar
     * @return {@code true} si ambas estaciones tienen el mismo nombre
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Station)) return false;
        Station s = (Station) obj;
        return this.name.equals(s.getName());
    }
}
