package edu.project.server.model.passenger;

import java.io.Serializable;

public class Passenger implements Serializable {

    private String id;
    private String names;

    public Passenger(String id, String names){
        this.id = id;
        this.names = names;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getId() {
        return id;
    }

}
