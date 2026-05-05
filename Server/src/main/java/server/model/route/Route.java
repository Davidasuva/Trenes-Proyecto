package server.model.route;
import edu.uva.app.queue.list.Queue;
import server.model.train.Train;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa una ruta de tren entre dos estaciones del sistema.
 * <p>
 * Una ruta define el trayecto (origen → destino), las fechas de salida y llegada,
 * la lista de trenes asignados (como cola FIFO), y calcula automáticamente la
 * distancia total usando el algoritmo de camino más corto del {@link RouteGraph}.
 * </p>
 *
 * @author Equipo Dinamita
 * @version 1.0.0
 * @see Station
 * @see RouteGraph
 * @see Train
 */
public class Route implements Serializable, Comparable<Route> {
    private static final long serialVersionUID = 4L;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private String name;
    private Queue<Train> trains;
    private LocalDateTime dateTravel;
    private LocalDateTime dateArrival;
    private Station origin;
    private Station destiny;
    private boolean active;
    private double totalDistance;

    /**
     * Construye una ruta y calcula la distancia total usando el grafo de rutas.
     *
     * @param id          identificador único de la ruta
     * @param name        nombre descriptivo de la ruta
     * @param trains      cola de trenes asignados a la ruta
     * @param dateTravel  fecha y hora de salida
     * @param dateArrival fecha y hora de llegada estimada
     * @param origin      estación de origen
     * @param destiny     estación de destino
     * @param routeGraph  grafo del sistema para calcular la distancia mínima
     * @throws RemoteException si falla la comunicación con el grafo remoto
     */
    public Route(int id, String name, Queue<Train> trains,
                 LocalDateTime dateTravel, LocalDateTime dateArrival,
                 Station origin, Station destiny, RouteGraph routeGraph) throws RemoteException {
        this.id = id;
        this.name = name;
        this.trains = trains;
        this.dateTravel = dateTravel;
        this.dateArrival = dateArrival;
        this.active = true;
        this.destiny = destiny;
        this.origin = origin;
        this.totalDistance = routeGraph.getShortestDistance(origin, destiny);
    }

    /**
     * Agrega un tren a la cola de trenes de la ruta.
     *
     * @param train tren a agregar
     * @return {@code true} si se insertó correctamente
     */
    public boolean addTrain(Train train) {
        try { trains.insert(train); return true; }
        catch (Exception e) { return false; }
    }

    /**
     * Extrae y retorna el primer tren de la cola (FIFO).
     *
     * @return tren extraído, o {@code null} si la cola está vacía o hubo error
     */
    public Train removeTrain() {
        try { return trains.extract(); }
        catch (Exception e) { return null; }
    }

    /** @return {@code true} si la ruta está activa */
    public boolean isActive() { return active; }

    /** @param active nuevo estado de la ruta */
    public void setActive(boolean active) { this.active = active; }

    /** @return ID único de la ruta */
    public int getId() { return id; }

    /** @param id nuevo ID */
    public void setId(int id) { this.id = id; }

    /** @return nombre de la ruta */
    public String getName() { return name; }

    /** @param name nuevo nombre */
    public void setName(String name) { this.name = name; }

    /** @return cola de trenes asignados */
    public Queue<Train> getTrains() { return trains; }

    /** @param trains nueva cola de trenes */
    public void setTrains(Queue<Train> trains) { this.trains = trains; }

    /** @return fecha y hora de salida */
    public LocalDateTime getDateTravel() { return dateTravel; }

    /** @param dateTravel nueva fecha/hora de salida */
    public void setDateTravel(LocalDateTime dateTravel) { this.dateTravel = dateTravel; }

    /** @return fecha y hora de llegada */
    public LocalDateTime getDateArrival() { return dateArrival; }

    /** @param dateArrival nueva fecha/hora de llegada */
    public void setDateArrival(LocalDateTime dateArrival) { this.dateArrival = dateArrival; }

    /**
     * Retorna la fecha de salida formateada como {@code dd/MM/yyyy HH:mm}.
     *
     * @return fecha de salida formateada, o cadena vacía si es nula
     */
    public String getDateTravelStr() {
        return dateTravel != null ? dateTravel.format(FORMATTER) : "";
    }

    /**
     * Retorna la fecha de llegada formateada como {@code dd/MM/yyyy HH:mm}.
     *
     * @return fecha de llegada formateada, o cadena vacía si es nula
     */
    public String getDateArrivalStr() {
        return dateArrival != null ? dateArrival.format(FORMATTER) : "";
    }

    /** @return distancia total del recorrido en kilómetros */
    public double getTotalDistance() { return totalDistance; }

    /** @return estación de destino */
    public Station getDestiny() { return destiny; }

    /** @param destiny nueva estación de destino */
    public void setDestiny(Station destiny) { this.destiny = destiny; }

    /** @return estación de origen */
    public Station getOrigin() { return origin; }

    /** @param origin nueva estación de origen */
    public void setOrigin(Station origin) { this.origin = origin; }

    /**
     * Compara dos rutas por su ID numérico.
     *
     * @param obj objeto a comparar
     * @return {@code true} si ambas rutas tienen el mismo ID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Route)) return false;
        Route r = (Route) obj;
        return this.id == r.getId();
    }

    @Override
    public int compareTo(Route r) {
        return Integer.compare(this.id, r.getId());
    }
}
