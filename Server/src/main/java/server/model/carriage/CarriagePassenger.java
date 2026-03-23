package server.model.carriage;

import java.io.Serializable;
import server.model.ticket.Ticket;
import edu.uva.app.priorityQueue.PriorityQueue;
public class CarriagePassenger extends AbstractCarriage implements Serializable {

    private PriorityQueue<Ticket> passengers;
    private int maxCapacity;
    private int actualCapacity;

    public CarriagePassenger(int id, int maxCapacity) {
        super(id);
        this.maxCapacity = maxCapacity;
        passengers=new PriorityQueue<>(3);
        actualCapacity=0;
    }

    public boolean addPassenger(Ticket passenger) {
        if(actualCapacity<maxCapacity){
            actualCapacity++;
            return passengers.insert(passenger.getCategory(),passenger);
        }else{
            return false;
        }
    }

    public int getNumberOfPassengers(){
        return actualCapacity;
    }


}
