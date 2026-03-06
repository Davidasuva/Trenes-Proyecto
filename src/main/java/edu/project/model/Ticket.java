package edu.project.model;

import java.io.Serializable;

public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    String id;
    String name;

    public Ticket(String id, String name){
        this.id=id;
        this.name=name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
