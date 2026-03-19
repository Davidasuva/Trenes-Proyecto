package edu.project.model.ticket;

import edu.project.model.carriage.CarriagePassenger;
import edu.project.model.luggage.Luggage;
import edu.project.model.route.Route;
import edu.project.server.model.passenger.Passenger;
import edu.project.model.train.Train;
import edu.uva.app.array.Array;

import java.io.Serializable;

public class Ticket implements Serializable {
    private int idTicket;
    private Passenger passenger;
    private Train train;
    private Route route;
    private CarriagePassenger carriagePassenger;
    private String category;
    private int price;
    private boolean state;
    private String dateBuy;
    private Array<Luggage>  luggages;


    public Ticket(String dateBuy, String category, boolean state, CarriagePassenger carriagePassenger, Route route, Passenger passenger, int idTicket) {
        this.dateBuy = dateBuy;
        this.category = category;
        this.state = state;
        this.carriagePassenger = carriagePassenger;
        this.route = route;
        this.train=route.getTrain();
        this.passenger = passenger;
        this.idTicket = idTicket;
        luggages=new Array<>(2);
    }

    public boolean calculatePrice(){
        return false;
    }
    public boolean verificateLuggage(){
        return false;
    }

    public boolean addLuggage(Luggage luggage){
        if(verificateLuggage()){
            return luggages.add(luggage);
        }
        return false;
    }

    public boolean removeLuggage(Luggage luggage){
        return luggages.remove(luggages.indexOf(luggage));
    }

    public Array<Luggage> getLuggages() {
        return luggages;
    }

    public String getDateBuy() {
        return dateBuy;
    }

    public boolean isState() {
        return state;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public CarriagePassenger getCarriagePassenger() {
        return carriagePassenger;
    }

    public Route getRoute() {
        return route;
    }

    public Train getTrain() {
        return train;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setCarriagePassenger(CarriagePassenger carriagePassenger) {
        this.carriagePassenger = carriagePassenger;
    }
}
