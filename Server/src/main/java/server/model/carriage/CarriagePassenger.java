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
        try{
            if(actualCapacity<maxCapacity){
                actualCapacity++;
                return passengers.insert(passenger.getCategory(),passenger);
            }else{
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public int getNumberOfPassengers(){
        return actualCapacity;
    }

    public PriorityQueue<Ticket> getPassengers() {
        return passengers;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public boolean hasMoreCapacity(){
        return actualCapacity<maxCapacity;
    }
    public int getActualCapacity() {
        return actualCapacity;
    }
}
