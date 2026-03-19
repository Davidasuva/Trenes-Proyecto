package edu.project.model.carriage;
import edu.project.model.ticket.Ticket;
import edu.uva.app.priorityQueue.PriorityQueue;
public class CarriagePassenger extends AbstractCarriage{

    private PriorityQueue<Ticket> passengers;
    private int maxCapacity;
    private int actualWeight;

    public CarriagePassenger(int idCarriage, int maxCapacity) {
        super(idCarriage);
        this.maxCapacity = maxCapacity;
        actualWeight = 0;
        passengers = new PriorityQueue<Ticket>(3);
    }


    public boolean addPassenger(Ticket ticket){
        if(actualWeight < maxCapacity){
            actualWeight++;
            return passengers.insert(ticket.getCategory(), ticket);
        }else{
            return false;
        }
    }

    public Ticket disembarkPassenger(){
        if(actualWeight > 0){
            actualWeight--;
            return passengers.extract();
        }else{
            return null;
        }
    }

    public int getNumberOfPassengers(){
        return actualWeight;
    }

}
