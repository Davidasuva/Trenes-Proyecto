package server.model.luggage;
import server.model.carriage.CarriageLoad;
import server.model.ticket.Ticket;
import java.io.Serializable;

public class Luggage implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int weight;
    private CarriageLoad carriage;
    private Ticket ticket;

    public Luggage(int id, int weight) {
        this.id = id;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    public CarriageLoad getCarriage() {
        return carriage;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setCarriage(CarriageLoad carriage) {
        this.carriage = carriage;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
