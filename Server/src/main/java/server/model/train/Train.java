package server.model.train;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import server.model.carriage.AbstractCarriage;
import java.io.Serializable;

public class Train implements Serializable {
    private static final long serialVersionUID = 3L;

    private int id;
    private String name;
    private String type;
    private int capacity;
    private int mileage;
    private LinkedList<AbstractCarriage> carriages;

    public Train(int id, String name, String type, int capacity, int mileage) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.mileage = mileage;
    }

    public boolean addCarriage(AbstractCarriage carriage){
        return carriages.add(carriage);
    }

    public boolean removeCarriage(AbstractCarriage carriage){
        return carriages.remove(carriage);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMileage() {
        return mileage;
    }

    public LinkedList<AbstractCarriage> getCarriages() {
        return carriages;
    }

    public void updateMileage(int mileage) {
        this.mileage += mileage;
    }
}





