package server.model.route;

import server.model.train.Train;

import java.io.Serializable;
import java.rmi.RemoteException;

public class Station implements Serializable {
    private static final long serialVersionUID = 5L;
    private int id;
    private String name;

    public Station(int id, String name) throws RemoteException {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Station{" + "id=" + id + ", name=" + name + '}';
    }
    @Override
    public boolean equals(Object obj) {
        if(this==obj){
            return true;
        }
        if(!(obj instanceof Station)){
            return false;
        }
        Station s=(Station)obj;
        return this.name.equals(s.getName());
    }
}

