package server.model.user;

import java.io.Serializable;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
public abstract class AbstractUser implements Serializable {
    private static final long serialVersionUID = 2L;
    private String id;
    private String name;
    private String mail;
    private String lastName;
    private String password;
    private String typeIdetification;
    private String adress;
    private int type;
    //1 Para usuario normal, 2 para Personal(Empleados), 3 para administradores
    private LinkedList<String> phoneNumbers;

    public AbstractUser(String id, String mail,String name, String lastName, String password, String typeIdetification, String adress, int type) {
        this.id = id;
        this.mail = mail;
        this.name = name;
        this.lastName = lastName;
        this.password = password;
        this.typeIdetification = typeIdetification;
        this.adress = adress;
        this.type = type;
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

    public int getType() {
        return type;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
