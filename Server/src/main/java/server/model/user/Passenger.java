package server.model.user;

import edu.uva.app.array.Array;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import server.model.carriage.AbstractCarriage;
import server.model.luggage.Luggage;
import server.model.ticket.Ticket;

public class Passenger extends AbstractUser{

    LinkedList<Ticket> historyTickets;
    Ticket actualTicket;
    Boolean isTraveling;

    public Passenger(String id, String mail ,String name, String lastName, String password, String typeIdetification, String adress) {
        super(id, mail,name, lastName, password, typeIdetification, adress,1);
        actualTicket = null;
        isTraveling = false;
        historyTickets = new LinkedList<>();
    }

    public boolean addTicket(Ticket ticket) {
        try{
            historyTickets.add(ticket);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public Ticket getActualTicket() {
        return actualTicket;
    }

    public void setActualTicket(Ticket actualTicket) {
        this.actualTicket = actualTicket;
    }

    public void setTraveling() {
        if(actualTicket != null){
            this.isTraveling = actualTicket.Status();
        }

    }

    public boolean IsTraveling() {
        return isTraveling;
    }

    public Array<Luggage> getLuggage() {
        return actualTicket.getLuggage();
    }
    public AbstractCarriage getCarriage(){
        return actualTicket.getCarriagePassenger();
    }
}
