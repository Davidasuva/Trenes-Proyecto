package server.model.route;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.app.queue.list.Queue;
import server.model.train.Train;

import java.io.Serializable;
public class Route implements Serializable {
    private static final long serialVersionUID = 4L;
    private int id;
    private String name;
    private Queue<Train> trains;
    private String dateTravel;
    private String dateArrival;
    private double totalDistance;

    public Route(int id, String name, Queue<Train> trains, String dateTravel, String dateArrival, double totalDistance) {
        this.id = id;
        this.name = name;
        this.trains = trains;
        this.dateTravel = dateTravel;
        this.dateArrival = dateArrival;
        this.totalDistance = totalDistance;
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

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }
}
