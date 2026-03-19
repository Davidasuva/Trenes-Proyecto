package edu.project.server.model.ticket;

import java.io.Serializable;
import edu.project.server.model.passenger.Passenger;

public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private Passenger passenger;

    public Ticket(String id, Passenger passenger){
        this.id=id;
        this.passenger=passenger;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return passenger.getNames();
    }
}
