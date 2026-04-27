package server.model.route;
import edu.uva.app.queue.list.Queue;
import server.model.train.Train;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Route implements Serializable {
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

    public boolean addTrain(Train train) {
        try {
            trains.insert(train);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Train removeTrain() {
        try {
            return trains.extract();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Queue<Train> getTrains() { return trains; }
    public void setTrains(Queue<Train> trains) { this.trains = trains; }

    public LocalDateTime getDateTravel() { return dateTravel; }
    public void setDateTravel(LocalDateTime dateTravel) { this.dateTravel = dateTravel; }

    public LocalDateTime getDateArrival() { return dateArrival; }
    public void setDateArrival(LocalDateTime dateArrival) { this.dateArrival = dateArrival; }

    public String getDateTravelStr() {
        return dateTravel != null ? dateTravel.format(FORMATTER) : "";
    }

    public String getDateArrivalStr() {
        return dateArrival != null ? dateArrival.format(FORMATTER) : "";
    }

    public double getTotalDistance() { return totalDistance; }

    public Station getDestiny() { return destiny; }
    public void setDestiny(Station destiny) { this.destiny = destiny; }

    public Station getOrigin() { return origin; }
    public void setOrigin(Station origin) { this.origin = origin; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Route)) return false;
        Route r = (Route) obj;
        return this.id == r.getId();
    }
}
