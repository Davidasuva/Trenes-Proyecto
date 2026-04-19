package server.model.route;
import edu.uva.app.queue.list.Queue;
import server.model.train.Train;

import java.io.Serializable;
import java.rmi.RemoteException;

public class Route implements Serializable {
    private static final long serialVersionUID = 4L;
    private int id;
    private String name;
    private Queue<Train> trains;
    private String dateTravel;
    private String dateArrival;
    private Station origin;
    private Station destiny;
    private boolean active;
    private double totalDistance;

    public Route(int id, String name, Queue<Train> trains, String dateTravel, String dateArrival,Station origin, Station destiny, RouteGraph routeGraph) throws RemoteException {
        this.id = id;
        this.name = name;
        this.trains = trains;
        this.dateTravel = dateTravel;
        this.dateArrival = dateArrival;
        this.active=true;
        this.destiny=destiny;
        this.origin=origin;
        this.totalDistance =routeGraph.getShortestDistance(origin, destiny) ;
    }

    public boolean addTrain(Train train){
        try{
            trains.insert(train);
            return true;
        }catch(Exception e){
            return false;
        }

    }

    public Train removeTrain(){
        try{
            return trains.extract();
        }catch(Exception e){
            return null;
        }

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Queue<Train> getTrains() {
        return trains;
    }

    public void setTrains(Queue<Train> trains) {
        this.trains = trains;
    }

    public String getDateTravel() {
        return dateTravel;
    }

    public void setDateTravel(String dateTravel) {
        this.dateTravel = dateTravel;
    }

    public String getDateArrival() {
        return dateArrival;
    }

    public void setDateArrival(String dateArrival) {
        this.dateArrival = dateArrival;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public Station getDestiny() {
        return destiny;
    }

    public void setDestiny(Station destiny) {
        this.destiny = destiny;
    }

    public Station getOrigin() {
        return origin;
    }

    public void setOrigin(Station origin) {
        this.origin = origin;
    }
    @Override
    public boolean equals(Object obj) {
        if(this==obj){
            return true;
        }
        if(!(obj instanceof Route)){
            return false;
        }
        Route r=(Route)obj;
        return this.id==r.getId();
    }
}
