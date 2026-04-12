package server.model.ticket;
import java.io.Serializable;
import edu.uva.app.array.Array;
import server.model.user.Passenger;
import server.model.carriage.CarriageLoad;
import server.model.train.Train;
import server.model.carriage.CarriagePassenger;
import server.model.luggage.Luggage;
import server.model.route.Route;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private Passenger passenger;
    private Route route;
    private Train train;
    private CarriagePassenger carriagePassenger;
    private CarriageLoad carriageLoad;
    private int category;
    private int price;
    private boolean status;
    private String dateBuy;
    private Array<Luggage> luggage;

    public Ticket(String id, Passenger passenger, Route route, Train train, CarriageLoad carriageLoad, CarriagePassenger carriagePassenger, int category, boolean status, String dateBuy) {
        this.id = id;
        this.passenger = passenger;
        this.route = route;
        this.train = train;
        this.carriagePassenger = carriagePassenger;
        this.carriageLoad = carriageLoad;
        this.category = category;
        this.status = status;
        this.dateBuy = dateBuy;
        luggage=new Array<>(2);
        passenger.addTicket(this);
    }

    public boolean verificateLuggage(Luggage luggage){
        return luggage.getWeight()<=80;
    }

    public boolean addLuggage(Luggage luggage){
        if(this.luggage.size()<2){
            if(verificateLuggage(luggage)&& carriageLoad.addLuggage(luggage)){
                return this.luggage.add(luggage);
            }
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public Route getRoute() {
        return route;
    }

    public Train getTrain() {
        return train;
    }

    public CarriagePassenger getCarriagePassenger() {
        return carriagePassenger;
    }

    public CarriageLoad getCarriageLoad() {
        return carriageLoad;
    }

    public int getCategory() {
        return category;
    }

    public int getPrice() {
        return price;
    }

    public boolean Status() {
        return status;
    }

    public String getDateBuy() {
        return dateBuy;
    }

    public Array<Luggage> getLuggage() {
        return luggage;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public void setCarriagePassenger(CarriagePassenger carriagePassenger) {
        this.carriagePassenger = carriagePassenger;
    }

    public void setCarriageLoad(CarriageLoad carriageLoad) {
        this.carriageLoad = carriageLoad;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setStatus(boolean status) {
        passenger.setTraveling();
        this.status = status;
    }

    public Passenger getPassenger() {
        return passenger;
    }
}
