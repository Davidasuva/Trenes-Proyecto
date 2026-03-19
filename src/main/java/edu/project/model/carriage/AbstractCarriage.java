package edu.project.model.carriage;

import java.io.Serializable;

public class AbstractCarriage implements Serializable {

    int idCarriage;

    public AbstractCarriage(int idCarriage) {
        this.idCarriage = idCarriage;
    }

    public int getIdCarriage() {
        return idCarriage;
    }

    public void setIdCarriage(int idCarriage) {
        this.idCarriage = idCarriage;
    }
}
