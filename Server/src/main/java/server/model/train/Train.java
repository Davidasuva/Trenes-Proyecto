package server.model.train;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import server.model.carriage.AbstractCarriage;
import java.io.Serializable;

public class Train implements Serializable {
    private static final long serialVersionUID = 3L;

    private int id;
    private String name;
    private String type;
    private int capacity;      // vagones de pasajeros
    private int cargoWagons;   // vagones de carga
    private int mileage;
    private LinkedList<AbstractCarriage> carriages;

    public Train(int id, String name, String type, int capacity, int cargoWagons, int mileage) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.cargoWagons = cargoWagons;
        this.mileage = mileage;
        carriages=new LinkedList<>();
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

    @Override
    public boolean equals(Object obj) {
        if(this==obj){
            return true;
        }
        if(!(obj instanceof Train)){
            return false;
        }
        Train t=(Train)obj;
        return this.id==t.getId();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCargoWagons() { return cargoWagons; }

    public void setCargoWagons(int cargoWagons) { this.cargoWagons = cargoWagons; }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}


