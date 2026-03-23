package server.model.ticket;
import java.io.Serializable;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
public class Passenger implements Serializable {
    private static final long serialVersionUID = 2L;

    private String id;
    private String name;
    private String lastName;
    private String password;
    private String typeIdetification;
    private String adress;
    private LinkedList<String> phoneNumbers;

    public Passenger(String id, String name, String lastName, String password, String typeIdetification, String adress) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.password = password;
        this.typeIdetification = typeIdetification;
        this.adress = adress;
        phoneNumbers = new LinkedList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTypeIdetification() {
        return typeIdetification;
    }

    public void setTypeIdetification(String typeIdetification) {
        this.typeIdetification = typeIdetification;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public boolean addPhoneNumber(String phoneNumber){
        return phoneNumbers.add(phoneNumber);
    }

    public boolean removePhoneNumber(String phoneNumber){
        return phoneNumbers.remove(phoneNumber);
    }
    public LinkedList<String> getPhoneNumbers() {
        return phoneNumbers;
    }
}
