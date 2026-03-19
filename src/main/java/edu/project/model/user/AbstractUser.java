package edu.project.model.user;

import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.project.model.emergencyContact.EmergencyContact;
import java.io.Serializable;

public abstract class AbstractUser implements User, Serializable{

    private String idUser;
    private String name;
    private String lastName;
    private String typeIdentification;
    private String adress;
    private String password;
    private LinkedList<String> phoneNumbers;
    private LinkedList<EmergencyContact> emergencyContacts;


    public AbstractUser(String idUser, String name, String lastName, String typeIdentification, String adress, String password) {
        this.idUser = idUser;
        this.name = name;
        this.lastName = lastName;
        this.typeIdentification = typeIdentification;
        this.adress = adress;
        this.password = password;
        phoneNumbers = new LinkedList<>();
        emergencyContacts = new LinkedList<>();
    }

    public boolean addNumber(String phoneNumber){
        return phoneNumbers.add(phoneNumber);
    }

    public boolean removeNumber(String phoneNumber){
        return phoneNumbers.remove(phoneNumber);
    }

    public boolean addEmergencyContact(EmergencyContact emergencyContact){
        return emergencyContacts.add(emergencyContact);
    }

    public boolean removeEmergencyContact(EmergencyContact emergencyContact){
        return emergencyContacts.remove(emergencyContact);
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
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

    public String getTypeIdentification() {
        return typeIdentification;
    }

    public void setTypeIdentification(String typeIdentification) {
        this.typeIdentification = typeIdentification;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LinkedList<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(LinkedList<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public LinkedList<EmergencyContact> getEmergencyContacts() {
        return emergencyContacts;
    }

    public void setEmergencyContacts(LinkedList<EmergencyContact> emergencyContacts) {
        this.emergencyContacts = emergencyContacts;
    }
}
